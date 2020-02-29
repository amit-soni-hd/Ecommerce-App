package com.ecommerce.ecommApp.commons.pojo.notification;

import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetails
{
    @JsonProperty("mode")
    private List<String> mode;

    @JsonProperty("order_id")
    private String OrderID;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("total_cost")
    private Float totalCost;

    @JsonProperty("customerDto")
    private CustomerDto customerDto;

    @Override
    public String toString() {
        return "OrderDetails{" +
                "mode=" + mode +
                ", OrderID='" + OrderID + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", totalCost=" + totalCost +
                ", customerDto=" + customerDto +
                '}';
    }
}

// example  :   {"mode":["Text_SMS","EMAIL"],"order_id":"ovq1","product_name":"ABC Product","quandity":3,"total_cost":3000,"customerDto":{"id":"ox1","name":"abc","number":8851530831,"email":"sagarbindal992@gmail.com","whatsapp":1234567890}}