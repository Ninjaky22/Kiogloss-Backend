package com.todoteg.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todoteg.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}