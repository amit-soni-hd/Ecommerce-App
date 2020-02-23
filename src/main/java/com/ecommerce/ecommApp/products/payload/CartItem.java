package com.ecommerce.ecommApp.products.payload;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CartItem {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("total_cost")
    private float cost;

}

