package com.todoteg.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.todoteg.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'published'")
    Page<Product> findAllPublished(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE (:status IS NULL OR p.status = :status) AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "EXISTS (SELECT t FROM p.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
           "EXISTS (SELECT v FROM p.variants v JOIN v.options o WHERE LOWER(o.value) LIKE LOWER(CONCAT('%', :search, '%'))))")
    Page<Product> searchByStatus(@Param("status") String status, @Param("search") String search, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p JOIN p.tags t WHERE p.status = 'published' AND LOWER(t.name) IN :tagNames")
    Page<Product> findPublishedByTagNames(@Param("tagNames") List<String> tagNames, Pageable pageable);
    
}