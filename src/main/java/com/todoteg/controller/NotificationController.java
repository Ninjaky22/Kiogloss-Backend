package com.todoteg.controller;

import com.todoteg.dto.NotificationDTO;
import com.todoteg.model.UserProfile;
import com.todoteg.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ==================== USUARIO ====================

    @GetMapping(value = "/user/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUser(Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        return notificationService.subscribe(user.getId());
    }

    @GetMapping("/user/notifications")
    public ResponseEntity<Page<NotificationDTO>> listUser(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(notificationService.getNotifications(user.getId(), pageable));
    }

    @GetMapping("/user/notifications/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCountUser(Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }

    @PatchMapping("/user/notifications/{id}/read")
    public ResponseEntity<Void> markReadUser(@PathVariable Long id, Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        notificationService.markRead(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/notifications/read-all")
    public ResponseEntity<Void> markAllReadUser(Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        notificationService.markAllRead(user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/notifications/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        notificationService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ==================== ADMIN ====================

    @GetMapping(value = "/admin/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public SseEmitter streamAdmin(Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        return notificationService.subscribe(user.getId());
    }

    @GetMapping("/admin/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<NotificationDTO>> listAdmin(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(notificationService.getNotifications(user.getId(), pageable));
    }

    @GetMapping("/admin/notifications/unread-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> unreadCountAdmin(Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }

    @PatchMapping("/admin/notifications/{id}/read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markReadAdmin(@PathVariable Long id, Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        notificationService.markRead(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/notifications/read-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markAllReadAdmin(Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        notificationService.markAllRead(user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/notifications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id, Authentication auth) {
        UserProfile user = (UserProfile) auth.getPrincipal();
        notificationService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
