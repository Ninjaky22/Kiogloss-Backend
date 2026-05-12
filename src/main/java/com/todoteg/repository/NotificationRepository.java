package com.todoteg.repository;

import com.todoteg.model.Notification;
import com.todoteg.model.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByRecipientIdAndReadFalse(Long userId);

    boolean existsByTypeAndRelatedEntityIdAndCreatedAtAfter(
            NotificationType type, Long relatedEntityId, LocalDateTime after);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP " +
           "WHERE n.recipient.id = :userId AND n.read = false")
    void markAllReadByUserId(@Param("userId") Long userId);
}
