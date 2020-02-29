package com.ecommerce.ecommApp.invoice.invoiceGenerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceFormatDto {

    @JsonProperty(value = "invoice_id", required = true)
    private String invoiceId;

    @JsonProperty(value = "customer_id", required = true)
    private Long customerId;

    @JsonProperty(value = "customer_name", required = true)
    private String customerName;

    @JsonProperty(value = "shipping_address", required = true)
    private String shippingAddress;

    @JsonProperty(value = "billing_address")
    private String billingAddress;

    @JsonProperty(value = "sold_by", required = true)
    private String soldBy;

    @JsonProperty(value = "invoice_details", required = true)
    private InvoiceDetails invoiceDetails;

    @JsonProperty(value = "title", defaultValue = "amazon.in")
    private String title;

}
