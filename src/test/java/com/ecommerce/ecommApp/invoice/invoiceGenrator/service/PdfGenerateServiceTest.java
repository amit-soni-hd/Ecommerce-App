package com.ecommerce.ecommApp.invoice.invoiceGenrator.service;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.pdfUtils.Utils;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.service.HeaderFooterService;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.service.PdfGenerateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PdfGenerateServiceTest {

    @InjectMocks
    private PdfGenerateService pdfGenerateService;

    @Spy
    private HeaderFooterService headerFooterService;

    private Objects objects;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.objects = new Objects();
    }

    @Test
    public void generatePdfTest() {

        InvoiceFormatDto invoiceFormatDto = objects.getInvoiceFormatDto();

        String filePath = pdfGenerateService.generatePdf(invoiceFormatDto);
        assertEquals(Utils.INVOICE_FOLDER + invoiceFormatDto.getCustomerName() +
                invoiceFormatDto.getInvoiceDetails().getProductId() + Utils.PDF_EXTENSION, filePath);
        verify(headerFooterService, times(1)).setHeader(anyString());
    }
}
