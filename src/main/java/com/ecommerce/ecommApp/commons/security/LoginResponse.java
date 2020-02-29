package com.ecommerce.ecommApp.commons.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("email_id")
    private String emailId;

    @JsonProperty("header")
    private String header;

    @JsonProperty("token")
    private String token;


}
