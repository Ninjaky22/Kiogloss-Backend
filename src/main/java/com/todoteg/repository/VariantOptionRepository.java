package com.todoteg.repository;

import com.todoteg.model.VariantOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantOptionRepository extends JpaRepository<VariantOption, Long> {
}
