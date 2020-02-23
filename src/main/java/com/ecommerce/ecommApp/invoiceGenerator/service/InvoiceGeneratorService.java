package com.ecommerce.ecommApp.invoiceGenerator.service;

import com.ecommerce.ecommApp.invoiceGenerator.converter.OrderDtoToInvoiceFormat;
import com.ecommerce.ecommApp.invoiceGenerator.dto.InvoiceRequestDto;
import com.ecommerce.ecommApp.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.customers.services.CustomerService;
import com.ecommerce.ecommApp.invoiceSend.dto.SendInvoiceDto;
import com.ecommerce.ecommApp.invoiceSend.utils.SendUtils;
import com.ecommerce.ecommApp.view.dto.response.ApiResponse;
import com.sendgrid.Response;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
@Service
public class InvoiceGeneratorService {

    private FetchOrderService fetchOrderService;
    private PdfGenerateService pdfGenerateService;
    private OrderDtoToInvoiceFormat toInvoiceFormat;
    private CustomerService customerService;
    private HttpServletRequest request;
    private WebClient.Builder builder;

    @Autowired
    public InvoiceGeneratorService(FetchOrderService fetchOrderService, PdfGenerateService pdfGenerateService,
                                   OrderDtoToInvoiceFormat orderDtoToInvoiceFormat, CustomerService customerService,
                                   HttpServletRequest request, WebClient.Builder builder) {
        this.pdfGenerateService = pdfGenerateService;
        this.fetchOrderService = fetchOrderService;
        this.toInvoiceFormat = orderDtoToInvoiceFormat;
        this.customerService = customerService;
        this.request = request;
        this.builder = builder;

    }


    public ApiResponse invoiceGenerate(InvoiceRequestDto invoiceRequestDto)  {

        Map<Long, Integer> responseStatus = new HashMap<>();


        List<OrdersDTO> ordersList = fetchOrderService.fetchOrder(invoiceRequestDto);
        log.trace("Fetch {} orders for customer Id {} ", ordersList.size(), invoiceRequestDto.getCustomerId());

        List<InvoiceFormatDto> invoiceFormatDtoList = toInvoiceFormat.convertToInvoiceFormatDto(ordersList);
        log.trace("Convert {} record to invoice format ", invoiceFormatDtoList.size());


        for (InvoiceFormatDto invoiceFormatDto: invoiceFormatDtoList) {

            String filePath = pdfGenerateService.generatePdf(invoiceFormatDto);
            Response response = sendMail(filePath, invoiceRequestDto.getCustomerId());
            responseStatus.put(invoiceFormatDto.getInvoiceDetails().getProductId(), response.getStatusCode());
        }

        log.info("Successfully send {} ", invoiceFormatDtoList.size());
        return new ApiResponse(HttpStatus.OK, "Successfully send invoice", responseStatus);
    }

    private SendInvoiceDto getDto(File file, Long customerId)  {

        CustomerDto customer = null;
        try {
            customer = customerService.getCustomerDetails(customerId);

        } catch (NotFoundException e) {
            log.info("Customer not found with cid : {}", customerId);
            e.printStackTrace();
            throw new RuntimeException("Customer not found exception :  " + e.getMessage() + "Cause : " + e.getCause());
        }

        SendInvoiceDto sendInvoiceDto = new SendInvoiceDto();

        sendInvoiceDto.setContentMessage(SendUtils.CONTENT_MESSAGE);
        sendInvoiceDto.setCustomerName(customer.getName());
        sendInvoiceDto.setSubject(SendUtils.SUBJECT);
        sendInvoiceDto.setFile(file);
        sendInvoiceDto.setTo(customer.getEmail());
        return sendInvoiceDto;
    }

    private String getBaseUrl() {

        StringBuilder address = new StringBuilder();

        address.append(request.getScheme());
        address.append("://");
        address.append(request.getLocalName().substring(4));
        address.append(":");
        address.append(request.getLocalPort());
        return address.toString();
    }

    private Response sendMail(String filePath, Long customerId)  {

        WebClient.Builder builder = this.builder;
        Optional<Response> response = builder
            .baseUrl(getBaseUrl())
            .build()
            .method(HttpMethod.GET)
            .uri(SendUtils.SEND_CONTROLLER_PATH)
            .body(BodyInserters.fromPublisher(Mono.just(getDto(new File(filePath), customerId)), SendInvoiceDto.class))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.forName("UTF-8"))
            .retrieve()
            .bodyToMono(Response.class)
        .blockOptional();

        return response.get();
    }
}
