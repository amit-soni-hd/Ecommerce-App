package com.ecommerce.ecommApp.commons.pojo.products;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "product")
@Data
public class Product {

  @JsonProperty("product_id")
  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long productId;

  @JsonProperty("category")
  @NotNull
  @Column
  private String category;

  @JsonProperty("product_name")
  @NotNull
  @Column(length = 1500)
  private String name;

  @JsonProperty
//  @NotNull
  @Column
  private String brand;

  @JsonProperty("product_description")
  @Column(length = 6000)
  private String productDescription;

  @JsonProperty("size")
  @Column
  private String size;

  @JsonProperty("quantity")
  @Column
  private int quantity;

  @JsonProperty("price")
  @Column
  private float price;

  @JsonProperty("product_image")
  @Column
  private String image;

  @Override
  public String toString() {
    return "Product{" +
            "productId=" + productId +
            ", category='" + category + '\'' +
            ", name='" + name + '\'' +
            ", brand='" + brand +'\'' +
            ", productDescription='" + productDescription + '\'' +
            ", size='" + size + '\'' +
            ", quantity=" + quantity +
            ", price=" + price +
            ", image='" + image + '\'' +
            '}';
  }

}


//  @ManyToMany(fetch = FetchType.EAGER,
//          cascade = {
//                  CascadeType.PERSIST,
//                  CascadeType.MERGE
//          })
//  @JoinTable(name = "product_cart",
//          joinColumns = { @JoinColumn(name = "productId") },
//          inverseJoinColumns = { @JoinColumn(name = "cartId") })

//  private Set<Cart> cart = new HashSet<>();
