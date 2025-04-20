package com.hryanmark.springbatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hryanmark.springbatch.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

}
