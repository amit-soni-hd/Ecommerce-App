package com.ecommerce.ecommApp.invoiceSend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.File;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendInvoiceDto {

    @NonNull
    @JsonProperty("file")
    private File file;

    @NonNull
    @JsonProperty("to")
    private String to;

    @NonNull
    @JsonProperty("customer_name")
    private String customerName;

    @NonNull
    @JsonProperty("subject")
    private String subject;

    @JsonProperty("content_message")
    private String contentMessage;


}
