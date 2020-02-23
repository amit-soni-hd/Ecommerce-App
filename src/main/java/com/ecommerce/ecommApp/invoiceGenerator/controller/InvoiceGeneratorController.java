package com.ecommerce.ecommApp.invoiceGenerator.controller;

import com.ecommerce.ecommApp.invoiceGenerator.dto.InvoiceRequestDto;
import com.ecommerce.ecommApp.invoiceGenerator.service.InvoiceGeneratorService;
import com.ecommerce.ecommApp.view.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/invoice")
public class InvoiceGeneratorController {

    @Autowired
    private InvoiceGeneratorService invoiceGenerate;

    @RequestMapping("/generator")
    public ResponseEntity<ApiResponse> invoiceGenerator(@Valid @RequestBody InvoiceRequestDto invoiceRequestDto) {

        log.info("Invoice generator called with customer id : " + invoiceRequestDto.getCustomerId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invoiceGenerate.invoiceGenerate(invoiceRequestDto));
    }
}
