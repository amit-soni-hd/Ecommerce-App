package com.ecommerce.ecommApp.invoiceGenerator.service;

import com.ecommerce.ecommApp.invoiceGenerator.dto.InvoiceDetails;
import com.ecommerce.ecommApp.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.invoiceGenerator.pdfUtils.Utils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.finance.tradukisto.MoneyConverters;
import com.ecommerce.ecommApp.exception.DocumentExceptionHandle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@Slf4j
@Service
public class PdfGenerateService {

    private HeaderFooterService headerFooterService;
    private PdfWriter pdfWriter;
    private Document document;

    @Autowired
    public PdfGenerateService(HeaderFooterService headerFooterService) {
        this.headerFooterService = headerFooterService;
    }


    public String generatePdf(InvoiceFormatDto invoiceFormatDto) {
        String filePath = null;
        try {
            if(!Files.exists(Paths.get(Utils.INVOICE_FOLDER)))
                Files.createDirectories(Paths.get(Utils.INVOICE_FOLDER));

            filePath = Utils.INVOICE_FOLDER + invoiceFormatDto.getCustomerName() +
                    invoiceFormatDto.getInvoiceDetails().getProductId() + Utils.PDF_EXTENSION;

            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document,
                    new FileOutputStream(
                            new File(filePath)));

        } catch (IOException | DocumentException e) {

            log.info("Exception occur with pid  {} and message {} and Cause is : {}", invoiceFormatDto.getInvoiceId()
                    , e.getMessage(), e.getCause());
            throw new RuntimeException("Exception : " + e.getMessage() + " Cause : " + e.getCause());
        }

        headerFooterService.setHeader(invoiceFormatDto.getTitle());
        pdfWriter.setPageEvent(headerFooterService);

        document.open();
            this.addMetaData(document, Utils.PDF_METADATA);
            this.addContent(document, invoiceFormatDto);

        document.close();

        return filePath;

    }


    private void addMetaData(Document document, String[] details) {

        document.addTitle(details[0]);
        document.addAuthor(details[1]);
        document.addCreationDate();
        document.addSubject(details[2]);
    }


    private void addContent(Document document, InvoiceFormatDto invoiceFormatDto)  {

        try {

            document.add(getInvoiceDetails(invoiceFormatDto.getInvoiceDetails()));
            document.add(addSoldBy(invoiceFormatDto.getSoldBy()));
            document.add(addAddresses(invoiceFormatDto));
            document.add(getProductDetails(invoiceFormatDto.getInvoiceDetails()));
            document.add(getAmountInWords(invoiceFormatDto));

        } catch (DocumentException e) {
            log.info("Document exception for add the content with PID {}", invoiceFormatDto.getInvoiceId());
            throw new DocumentExceptionHandle("Document Exception : " + e.getMessage() + "Cause : " + e.getCause());
        }

    }


    private Paragraph getInvoiceDetails(InvoiceDetails invoiceDetails) {

        Paragraph paragraph = getParagraph();

        paragraph.add(new Phrase("Invoice Id : ", Utils.NORMAL_BOLD));
        paragraph.add(new Phrase(invoiceDetails.getProductId().toString(), Utils.NORMAL_FONT));
        paragraph.add(new Phrase("\nInvoice generated : ", Utils.NORMAL_BOLD));
        paragraph.add(new Phrase(new Date().toString(), Utils.NORMAL_FONT));
        paragraph.add(new Paragraph(" "));
        return paragraph;
    }

    private Paragraph addSoldBy(String soldBy) {

        Paragraph vendor = getParagraph();

        vendor.add(new Chunk("Sold By : \n", Utils.NORMAL_BOLD));
        vendor.add(new Chunk(soldBy));
        vendor.add(new Paragraph(" "));
        vendor.add(new Paragraph(" "));
        return vendor;
    }

    private Paragraph addAddresses(InvoiceFormatDto invoiceFormatDto) {

        Paragraph address = new Paragraph();
        PdfPCell cell;
        PdfPTable table = new PdfPTable(2);

        address.setIndentationLeft(Utils.LEFT_INDENT);
        address.add(new Chunk("Addresses  : \n", Utils.NORMAL_BOLD));

        cell = new PdfPCell(addBillingAddress(invoiceFormatDto.getBillingAddress()));
        cell.enableBorderSide(0);
        table.addCell(cell);

        cell = new PdfPCell(addShippingAddress(invoiceFormatDto.getShippingAddress()));
        cell.enableBorderSide(0);
        table.addCell(cell);

        table.setPaddingTop(100);
        address.add(table);
        address.add(new Paragraph(" "));
        return address;

    }

    private Paragraph addBillingAddress(String billingAddress) {

        Paragraph billAddress = getParagraph();

        billAddress.add(new Chunk("Billing Address : \n\n", Utils.NORMAL_BOLD));
        billAddress.add(billingAddress);
        return billAddress;
    }

    private Paragraph addShippingAddress(String shippingAddress) {

        Paragraph shippingAdd = getParagraph();

        shippingAdd.add(new Chunk("Shipping Address : \n\n", Utils.NORMAL_BOLD));
        shippingAdd.add(shippingAddress);
        return shippingAdd;
    }


    private Paragraph getProductDetails(InvoiceDetails invoiceDetails) {

        Paragraph productDetails = getParagraph();
        PdfPTable table = createProductTable(invoiceDetails);

        productDetails.add(new Chunk("Product details : ", Utils.NORMAL_BOLD));
        productDetails.add(table);
        return productDetails;
    }

    private PdfPTable createProductTable(InvoiceDetails invoiceDetails) {

        PdfPTable table = new PdfPTable(6);

        table.setWidthPercentage(100);
        addHeaderInTable(table, Utils.HEADER_ARRAY);
        addDataInTable(table, invoiceDetails);
        return table;
    }

    private void addHeaderInTable(PdfPTable table, String[] headerArray) {

        PdfPCell cell = null;
        for (String header: headerArray) {
            cell = new PdfPCell(new Phrase(header, Utils.NORMAL_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

    }

    private void addDataInTable(PdfPTable table, InvoiceDetails invoiceDetails) {

        table.addCell(invoiceDetails.getOrderId());
        table.addCell(invoiceDetails.getProductName());
        table.addCell(invoiceDetails.getBrand());
        table.addCell(invoiceDetails.getPrice().toString());
        table.addCell(invoiceDetails.getQuantity().toString());
        table.addCell(String.valueOf(invoiceDetails.getPrice() * invoiceDetails.getQuantity()));
    }

    private Paragraph getAmountInWords(InvoiceFormatDto invoiceFormatDto) {

        Paragraph amountWords = getParagraph();
        amountWords.add(new Phrase("\n\nAmount in words : ", Utils.NORMAL_BOLD));

        MoneyConverters converter = MoneyConverters.ENGLISH_BANKING_MONEY_VALUE;
        String moneyAsWords = converter.asWords(
                new BigDecimal(invoiceFormatDto.getInvoiceDetails().getTotalAmount()));
        amountWords.add(new Phrase(moneyAsWords.toUpperCase(), Utils.SMALL_FONT));

        return amountWords;
    }


    private Paragraph getParagraph() {

        Paragraph paragraph = new Paragraph();

        paragraph.setIndentationLeft(Utils.LEFT_INDENT);
        paragraph.setFont(Utils.NORMAL_FONT);
        return paragraph;
    }

}
