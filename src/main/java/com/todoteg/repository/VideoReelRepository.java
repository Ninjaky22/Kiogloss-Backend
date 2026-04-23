package com.todoteg.repository;

import com.todoteg.model.VideoReel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoReelRepository extends JpaRepository<VideoReel, Long> {
    List<VideoReel> findAllByOrderByCreatedAtDesc();
    List<VideoReel> findByProductId(Long productId);

    @Query("SELECT v FROM VideoReel v WHERE v.product.status = 'published' ORDER BY v.createdAt DESC")
    List<VideoReel> findAllPublishedByOrderByCreatedAtDesc();
}
