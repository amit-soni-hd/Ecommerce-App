package com.ecommerce.ecommApp.commons.pojo.products;

import com.ecommerce.ecommApp.products.composite.CartIdentity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cart")
@Data
public class Cart {

    @EmbeddedId
    private CartIdentity cartIdentity;

    @JsonProperty("quantity")
    @Column(name = "quantity")
    private int quantity;

    @JsonProperty("total_cost")
    private float cost;

}
