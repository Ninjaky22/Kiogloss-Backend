package com.todoteg.repository;

import com.todoteg.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    
    @Query("SELECT DISTINCT o.id FROM ProductVariant pv JOIN pv.options o WHERE o.id IN :optionIds")
    Set<Long> findOptionIdsInUse(@Param("optionIds") Set<Long> optionIds);
}
