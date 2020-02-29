package com.ecommerce.ecommApp.invoice.invoiceGenrator.service;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.customers.services.CustomerService;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.converter.OrderDtoToInvoiceFormat;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.service.InvoiceGeneratorService;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.service.PdfGenerateService;
import com.ecommerce.ecommApp.invoice.invoiceSend.service.SendInvoiceService;
import com.sendgrid.Response;
import javassist.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceGeneratorServiceTest {

    @InjectMocks
    private InvoiceGeneratorService invoiceGeneratorService;

    @Mock
    private PdfGenerateService pdfGenerateService;

    @Mock
    private OrderDtoToInvoiceFormat orderDtoToInvoiceFormat;

    @Mock
    private CustomerService customerService;

    @Mock
    private SendInvoiceService sendInvoiceService;

    private Objects objects;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.objects = new Objects();
    }

    @Test
    public void invoiceGenerateTest() throws NotFoundException {

        InvoiceFormatDto invoiceFormatDto = objects.getInvoiceFormatDto();
        String filePath = objects.getFilePath();
        CustomerDto customerDto = objects.getCustomerDto();

        when(orderDtoToInvoiceFormat.convertToInvoiceFormatDto(any())).thenReturn(invoiceFormatDto);
        when(pdfGenerateService.generatePdf(any())).thenReturn(filePath);
        when(sendInvoiceService.sendInvoice(any())).thenReturn(new Response());
        when(customerService.getCustomerDetails(anyLong())).thenReturn(customerDto);

        invoiceGeneratorService.invoiceGenerate(any());

        verify(orderDtoToInvoiceFormat, times(1)).convertToInvoiceFormatDto(any());
        verify(pdfGenerateService, times(1)).generatePdf(any());
        verify(customerService, times(1)).getCustomerDetails(anyLong());
        verify(sendInvoiceService, times(1)).sendInvoice(any());

    }

    @Test(expected = RuntimeException.class)
    public void invoiceGenerateExceptionTest() throws NotFoundException {

        InvoiceFormatDto invoiceFormatDto = objects.getInvoiceFormatDto();
        String filePath = objects.getFilePath();

        when(orderDtoToInvoiceFormat.convertToInvoiceFormatDto(any())).thenReturn(invoiceFormatDto);
        when(pdfGenerateService.generatePdf(any())).thenReturn(filePath);
        when(customerService.getCustomerDetails(anyLong())).thenThrow(NotFoundException.class);

        invoiceGeneratorService.invoiceGenerate(any());

        verify(orderDtoToInvoiceFormat, times(1)).convertToInvoiceFormatDto(any());
        verify(pdfGenerateService, times(1)).generatePdf(any());
        verify(customerService, times(1)).getCustomerDetails(anyLong());
        verify(sendInvoiceService, times(0)).sendInvoice(any());

    }

}
