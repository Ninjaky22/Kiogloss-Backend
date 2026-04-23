package com.todoteg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todoteg.model.Images;

public interface ImagesRepository extends JpaRepository<Images, Long> {
    List<Images> findByProductId(Long productId);
}