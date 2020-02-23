package com.ecommerce.ecommApp.customers.dto;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Data Transfer Object used for login.
 */
@Data
public class LoginDto {

    @NotEmpty
    @NotNull
    private String email;

    @NotNull
    @NotEmpty
    private String password;
}
