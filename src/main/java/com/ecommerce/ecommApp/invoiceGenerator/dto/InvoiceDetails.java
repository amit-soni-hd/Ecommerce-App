package com.ecommerce.ecommApp.invoiceGenerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetails {

    @JsonProperty(value = "order_id", required = true)
    private String orderId;

    @JsonProperty(value = "product_id", required = true)
    private Long productId;

    @JsonProperty(value = "product_name", required = true)
    private String productName;

    @JsonProperty(value = "quantity", required = true)
    private Long quantity;

    @JsonProperty(value = "brand")
    private String brand;

    @JsonProperty(value = "product_description")
    private String product_description;

    @JsonProperty(value = "product_image")
    private String productImage;

    @JsonProperty(value = "price", required = true)
    private Float price;

    @JsonProperty(value = "total_amount", required = true)
    private Double totalAmount;

}
