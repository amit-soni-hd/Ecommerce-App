package com.ecommerce.ecommApp.invoice.invoiceGenerator.converter;

import com.ecommerce.ecommApp.commons.exceptions.ElementNotFoundException;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.dto.InvoiceDetails;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.products.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderDtoToInvoiceFormat {

    @Autowired
    private ProductService productService;

    /**
     * method to convert the orders details to invoice format
     * @param ordersDTO contain the details of order
     * @return object of invoice format contain the details of invoice document
     */
    public InvoiceFormatDto convertToInvoiceFormatDto(OrdersDTO ordersDTO) {


            InvoiceFormatDto invoiceFormatDto = new InvoiceFormatDto();
            InvoiceDetails invoiceDetails = new InvoiceDetails();
            try {

                Product product = productService.getProduct(ordersDTO.getProductID());

                invoiceDetails.setBrand(product.getBrand());
                invoiceDetails.setOrderId(ordersDTO.getOrderID());
                invoiceDetails.setPrice(product.getPrice());
                invoiceDetails.setProductDescription(product.getProductDescription());
                invoiceDetails.setProductId(ordersDTO.getProductID());
                invoiceDetails.setProductImage(product.getImage());
                invoiceDetails.setProductName(product.getName());
                invoiceDetails.setQuantity(Long.valueOf(ordersDTO.getQuantity()));
                invoiceDetails.setTotalAmount(Double.valueOf(ordersDTO.getQuantity() * product.getPrice()));

                invoiceFormatDto.setCustomerId(ordersDTO.getCustomerID());
                invoiceFormatDto.setCustomerName("Amit");
                invoiceFormatDto.setInvoiceId(UUID.randomUUID().toString());
                invoiceFormatDto.setShippingAddress("shipping address");
                invoiceFormatDto.setBillingAddress("billing address");
                invoiceFormatDto.setSoldBy("Amit");
                invoiceFormatDto.setInvoiceDetails(invoiceDetails);
                invoiceFormatDto.setTitle("Amazon.in");

            } catch (ElementNotFoundException | InterruptedException e) {
                log.error("Element not found exception : " + e.getMessage() + "\nCause : " + e.getCause());
                throw new RuntimeException("Product not found in data base with productID" + ordersDTO.getProductID() +
                        "Message : " + e.getMessage() + "Cause : " + e.getCause());
            }

        return invoiceFormatDto;
    }
}
