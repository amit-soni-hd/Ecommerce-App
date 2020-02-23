package com.ecommerce.ecommApp.commons.pojo.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemsDTO {

    @JsonProperty("product_id")
    private long productID;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("total_cost")
    private float cost;
}
