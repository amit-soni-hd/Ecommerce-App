package com.ecommerce.ecommApp.invoiceGenerator.converter;

import com.ecommerce.ecommApp.invoiceGenerator.dto.InvoiceDetails;
import com.ecommerce.ecommApp.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.products.exceptions.ElementNotFoundException;
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

    public List<InvoiceFormatDto> convertToInvoiceFormatDto(List<OrdersDTO> ordersDTOList) {

        List<InvoiceFormatDto> list = new ArrayList<>();

        ordersDTOList.forEach(ordersDTO -> {
            InvoiceFormatDto invoiceFormatDto = new InvoiceFormatDto();
            InvoiceDetails invoiceDetails = new InvoiceDetails();
            try {

                Product product = productService.getProduct(ordersDTO.getProductID());

                invoiceDetails.setBrand(product.getBrand());
                invoiceDetails.setOrderId(ordersDTO.getOrderID());
                invoiceDetails.setPrice(product.getPrice());
                invoiceDetails.setProduct_description(product.getProductDescription());
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

                list.add(invoiceFormatDto);

            } catch (ElementNotFoundException e) {
                log.error("Element not found exception : " + e.getMessage() + "\nCause : " + e.getCause());
            }

        });
        return list;
    }
}
