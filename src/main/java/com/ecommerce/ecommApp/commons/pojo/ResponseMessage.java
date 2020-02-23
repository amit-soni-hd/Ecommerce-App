package com.ecommerce.ecommApp.commons.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseMessage {
    @JsonProperty(value="message")
    private String message;
    private String type;

    public ResponseMessage(String message, String type){
        this.message = message;
        this.type = type;
    }
}
