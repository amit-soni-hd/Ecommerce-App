package com.ecommerce.ecommApp.customers.dto;

import com.ecommerce.ecommApp.customers.customAnnotations.PasswordMatches;
import com.ecommerce.ecommApp.customers.customAnnotations.ValidEmail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


/**
 * Data Transfer Object used at the time of registration.
 */
@Data
@PasswordMatches
public class RegistrationDto
{
    @JsonProperty("name")
    @NotEmpty
    private String name;

    @JsonProperty("password")
    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    @JsonProperty("matching_password")
    private String matchingPassword;

    @JsonProperty("email")
    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;

    @JsonProperty("number")
    @NotNull(message = "Please enter number")
    private Long number;

    @JsonProperty("whatsapp")
    private Long whatsapp;

    @JsonProperty("gender")
    @NotNull
    @NotEmpty
    private String gender;
}
