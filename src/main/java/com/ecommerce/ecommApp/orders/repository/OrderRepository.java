package com.ecommerce.ecommApp.orders.repository;

import com.ecommerce.ecommApp.orders.Models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {
    /**
     * Custom query to get all the orders of a particular customer
     *
     * @param customerID
     * @return List of orders
     */
    @Query(value = "Select * from orders where customer_id=:customerID", nativeQuery = true)
    List<Orders> getOrdersByCustomerId(@Param("customerID") Long customerID);
}
