package com.todoteg.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todoteg.model.DetailFavoritos;

public interface DetailFavoritosRepository extends JpaRepository<DetailFavoritos, Long> {
    List<DetailFavoritos> findByAccountId(Long accountId);
    Optional<DetailFavoritos> findByAccountIdAndProductId(Long accountId, Long productId);
}