package com.todoteg.repository;

import com.todoteg.model.VariantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantTypeRepository extends JpaRepository<VariantType, Long> {
}
