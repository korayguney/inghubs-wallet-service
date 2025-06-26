package com.inghubs.walletservice.repository;

import com.inghubs.walletservice.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByTckn(String tckn);

    Customer findByTckn(String tckn);
}