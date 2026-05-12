package com.todoteg.service;

import com.todoteg.dto.NotificationDTO;
import com.todoteg.model.*;
import com.todoteg.repository.NotificationRepository;
import com.todoteg.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserProfileRepository userProfileRepository;

    @Value("${notifications.low-stock-threshold:5}")
    private int lowStockThreshold;

    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // ==================== SSE ====================

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(30L * 60 * 1000); // 30 min timeout
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId, emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(userId, emitter);
        });
        emitter.onError(e -> emitters.remove(userId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(userId, emitter);
        }

        return emitter;
    }

    @Scheduled(fixedRate = 25000)
    public void sendHeartbeat() {
        List<Long> stale = new ArrayList<>();
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().comment("heartbeat"));
            } catch (Exception e) {
                stale.add(userId);
            }
        });
        stale.forEach(emitters::remove);
    }

    // ==================== BUSINESS EVENTS ====================

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyOrderCreated(Order order) {
        String msg = "Orden #" + order.getId() + " de " + order.getAccount().getUser().getName()
                + " por COP " + String.format("%,.0f", order.getAmount());
        List<UserProfile> admins = userProfileRepository.findByIsSuperuserTrue();
        for (UserProfile admin : admins) {
            Notification n = buildNotification(admin, "ADMIN", NotificationType.ORDER_CREATED,
                    "Nueva orden recibida", msg, "ORDER", order.getId());
            notificationRepository.save(n);
            pushToClient(admin.getId(), n);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyOrderStatusChanged(Order order) {
        String statusLabel = order.getStatus().getDescription();
        String msg = "Tu orden #" + order.getId() + " ahora está: " + statusLabel;
        UserProfile user = order.getAccount().getUser();
        Notification n = buildNotification(user, "USER", NotificationType.ORDER_STATUS_CHANGED,
                "Estado de tu pedido actualizado", msg, "ORDER", order.getId());
        notificationRepository.save(n);
        pushToClient(user.getId(), n);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkAndNotifyStock(ProductVariant variant) {
        int stock = variant.getStock();
        String productName = variant.getProduct().getTitle();

        if (stock == 0) {
            boolean recentExists = notificationRepository.existsByTypeAndRelatedEntityIdAndCreatedAtAfter(
                    NotificationType.OUT_OF_STOCK, variant.getId(), LocalDateTime.now().minusHours(1));
            if (!recentExists) {
                String msg = productName + " (var. #" + variant.getId() + ") se quedó sin stock";
                fanOutToAdmins(NotificationType.OUT_OF_STOCK, "Producto sin stock", msg,
                        "PRODUCT_VARIANT", variant.getId());
            }
        } else if (stock <= lowStockThreshold) {
            boolean recentExists = notificationRepository.existsByTypeAndRelatedEntityIdAndCreatedAtAfter(
                    NotificationType.LOW_STOCK, variant.getId(), LocalDateTime.now().minusHours(1));
            if (!recentExists) {
                String msg = productName + " tiene solo " + stock + " unidades disponibles";
                fanOutToAdmins(NotificationType.LOW_STOCK, "Stock bajo", msg,
                        "PRODUCT_VARIANT", variant.getId());
            }
        }
    }

    private void fanOutToAdmins(NotificationType type, String title, String message,
                                 String entityType, Long entityId) {
        List<UserProfile> admins = userProfileRepository.findByIsSuperuserTrue();
        for (UserProfile admin : admins) {
            Notification n = buildNotification(admin, "ADMIN", type, title, message, entityType, entityId);
            notificationRepository.save(n);
            pushToClient(admin.getId(), n);
        }
    }

    // ==================== REST METHODS ====================

    public Page<NotificationDTO> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDTO);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Transactional
    public void markRead(Long notifId, Long userId) {
        notificationRepository.findById(notifId).ifPresent(n -> {
            if (n.getRecipient().getId().equals(userId) && !n.isRead()) {
                n.setRead(true);
                n.setReadAt(LocalDateTime.now());
                notificationRepository.save(n);
            }
        });
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

    @Transactional
    public void delete(Long notifId, Long userId) {
        notificationRepository.findById(notifId).ifPresent(n -> {
            if (n.getRecipient().getId().equals(userId)) {
                notificationRepository.delete(n);
            }
        });
    }

    // ==================== HELPERS ====================

    private void pushToClient(Long userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(toDTO(notification)));
        } catch (Exception e) {
            emitters.remove(userId, emitter);
        }
    }

    private Notification buildNotification(UserProfile recipient, String scope,
                                            NotificationType type, String title, String message,
                                            String entityType, Long entityId) {
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setRecipientScope(scope);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setRelatedEntityType(entityType);
        n.setRelatedEntityId(entityId);
        return n;
    }

    public NotificationDTO toDTO(Notification n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setRelatedEntityType(n.getRelatedEntityType());
        dto.setRelatedEntityId(n.getRelatedEntityId());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
