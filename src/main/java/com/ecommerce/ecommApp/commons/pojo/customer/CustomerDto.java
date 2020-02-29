package com.ecommerce.ecommApp.commons.pojo.customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("number")
    private Long number;

    @JsonProperty("whatsapp")
    private Long whatsapp;

    @JsonProperty("gender")
    private String gender;

}
