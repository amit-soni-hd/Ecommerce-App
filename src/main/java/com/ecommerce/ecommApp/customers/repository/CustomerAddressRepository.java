package com.ecommerce.ecommApp.customers.repository;

import com.ecommerce.ecommApp.customers.models.Customer;
import com.ecommerce.ecommApp.customers.models.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findByCustomer(Customer customer);
}
