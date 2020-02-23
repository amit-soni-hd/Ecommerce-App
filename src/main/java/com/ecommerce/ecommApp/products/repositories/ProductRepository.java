package com.ecommerce.ecommApp.products.repositories;

import com.ecommerce.ecommApp.commons.pojo.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query(value = "SELECT distinct(category) from product", nativeQuery = true)
  List<String> getCategory();

  @Query(value = "SELECT * from product WHERE product.category = ?1", nativeQuery = true)
  List<Product> getElementsByCategory(String category);
}