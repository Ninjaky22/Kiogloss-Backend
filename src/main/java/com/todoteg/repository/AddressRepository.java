package com.todoteg.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todoteg.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
