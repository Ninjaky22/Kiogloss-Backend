package com.todoteg.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todoteg.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);
}