package com.ecommerce.ecommApp.invoice.invoiceGenerator.service;

import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.customers.services.CustomerService;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.converter.OrderDtoToInvoiceFormat;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.invoice.invoiceSend.dto.SendInvoiceDto;
import com.ecommerce.ecommApp.invoice.invoiceSend.service.SendInvoiceService;
import com.ecommerce.ecommApp.invoice.invoiceSend.utils.SendUtils;
import com.sendgrid.Response;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class InvoiceGeneratorService {

    private PdfGenerateService pdfGenerateService;
    private OrderDtoToInvoiceFormat toInvoiceFormat;
    private CustomerService customerService;
    private SendInvoiceService sendInvoiceService;

    /**
     * constructor for initialize the local variable
     * @param pdfGenerateService
     * @param orderDtoToInvoiceFormat
     * @param customerService
     * @param sendInvoiceService
     */
    @Autowired
    public InvoiceGeneratorService(PdfGenerateService pdfGenerateService,
                                   OrderDtoToInvoiceFormat orderDtoToInvoiceFormat, CustomerService customerService,
                                   SendInvoiceService sendInvoiceService) {
        this.pdfGenerateService = pdfGenerateService;
        this.toInvoiceFormat = orderDtoToInvoiceFormat;
        this.customerService = customerService;
        this.sendInvoiceService = sendInvoiceService;

    }

    /**
     * method for generate the invoice in pdf format
     * @param ordersDTO contain the details of order
     */
    public Response invoiceGenerate(OrdersDTO ordersDTO)  {

        InvoiceFormatDto invoiceFormatDto = toInvoiceFormat.convertToInvoiceFormatDto(ordersDTO);

        log.trace("Convert record to invoice format for customerId {} and productId {}", invoiceFormatDto.getCustomerId(),
                invoiceFormatDto.getInvoiceDetails().getProductId());

        String filePath = pdfGenerateService.generatePdf(invoiceFormatDto);
        log.info("Generate invoice for customer id {} with pid {}", invoiceFormatDto.getCustomerId(),
                invoiceFormatDto.getInvoiceDetails().getProductId());

        Response response = sendInvoiceService.sendInvoice(getDto(filePath, invoiceFormatDto.getCustomerId()));
        log.info("Send invoice for customerId {} with status code {}", invoiceFormatDto.getCustomerId(),
                response.getStatusCode());

        return response;
    }

    /**
     * method for creating the object SendInvoiceDto which contain the details to be send
     * @param filePath file path of invoice
     * @param customerId unique id of user
     * @return object of SendInvoiceDto contains message, file, email of sender, email of recipient  etc...
     */
    private SendInvoiceDto getDto(String filePath, Long customerId)  {

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
        sendInvoiceDto.setFile(new File(filePath));
        sendInvoiceDto.setTo(customer.getEmail());
        return sendInvoiceDto;
    }
}
