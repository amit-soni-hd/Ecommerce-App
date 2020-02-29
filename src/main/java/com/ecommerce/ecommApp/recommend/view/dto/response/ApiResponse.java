package com.ecommerce.ecommApp.recommend.view.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private HttpStatus status;
    private String message;
    private Object data;

    public ApiResponse(String message){
        this.message = message;
    }

    public ApiResponse(HttpStatus status) {
        this.status = status;
    }

    public ApiResponse(HttpStatus status, String message){
        this.status = status;
        this.message = message;
    }
}
