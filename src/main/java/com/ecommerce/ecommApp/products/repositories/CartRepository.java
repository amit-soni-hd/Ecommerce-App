package com.ecommerce.ecommApp.products.repositories;

import com.ecommerce.ecommApp.commons.pojo.products.Cart;
import com.ecommerce.ecommApp.products.composite.CartIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, CartIdentity> {
    @Query(nativeQuery = true, value = "select * from cart where customer_id=?1 ")
    Optional<List<Cart>> findByCustomerId(Long customerId);

}
