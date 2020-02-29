package com.ecommerce.ecommApp;

import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderDetails;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.dto.InvoiceDetails;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.invoice.invoiceSend.dto.SendInvoiceDto;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Objects {

    public OrdersDTO getOrderDto() {

        OrdersDTO ordersDTO = new OrdersDTO();

        ordersDTO.setQuantity(1);
        ordersDTO.setStatus("Placed");
        ordersDTO.setProductID(1);
        ordersDTO.setCost(500);
        ordersDTO.setCustomerID(1);
        ordersDTO.setOrderID("axy12");
        return ordersDTO;
    }

    public Product getProduct() {

        Product product = new Product();

        product.setSize("M");
        product.setImage("blank");
        product.setProductDescription("black t-shirt");
        product.setProductId(1);
        product.setPrice(500);
        product.setName("t-shirt");
        product.setCategory("puma");
        product.setQuantity(10);
        product.setBrand("puma");
        return product;
    }

    public InvoiceDetails getInvoiceDetails() {

        InvoiceDetails invoiceDetails = new InvoiceDetails();
        Product product = getProduct();
        OrdersDTO ordersDTO = getOrderDto();

        invoiceDetails.setBrand(product.getBrand());
        invoiceDetails.setOrderId(ordersDTO.getOrderID());
        invoiceDetails.setPrice(product.getPrice());
        invoiceDetails.setProductDescription(product.getProductDescription());
        invoiceDetails.setProductId(ordersDTO.getProductID());
        invoiceDetails.setProductImage(product.getImage());
        invoiceDetails.setProductName(product.getName());
        invoiceDetails.setQuantity(Long.valueOf(ordersDTO.getQuantity()));
        invoiceDetails.setTotalAmount(Double.valueOf(ordersDTO.getQuantity() * product.getPrice()));

        return invoiceDetails;
    }

    public InvoiceFormatDto getInvoiceFormatDto() {

        InvoiceFormatDto invoiceFormatDto = new InvoiceFormatDto();
        InvoiceDetails invoiceDetails = getInvoiceDetails();
        OrdersDTO ordersDTO = getOrderDto();

        invoiceFormatDto.setCustomerId(ordersDTO.getCustomerID());
        invoiceFormatDto.setCustomerName("Amit");
        invoiceFormatDto.setInvoiceId(UUID.randomUUID().toString());
        invoiceFormatDto.setShippingAddress("shipping address");
        invoiceFormatDto.setBillingAddress("billing address");
        invoiceFormatDto.setSoldBy("Amit");
        invoiceFormatDto.setInvoiceDetails(invoiceDetails);
        invoiceFormatDto.setTitle("Amazon.in");
        return invoiceFormatDto;
    }

    public String getFilePath() {
        String filePath = "/invoice/filename";
        return filePath;
    }

    public CustomerDto getCustomerDto() {

        CustomerDto customerDto = new CustomerDto();

        customerDto.setEmail("verma6543@gmail.com");
        customerDto.setGender("M");
        customerDto.setId(1L);
        customerDto.setName("Amit");
        customerDto.setNumber(Long.valueOf(1234567890));
        customerDto.setWhatsapp(Long.valueOf(1234567890));
        return customerDto;
    }

    public SendInvoiceDto getSendInvoiceDto() {

        SendInvoiceDto sendInvoiceDto = new SendInvoiceDto();

        sendInvoiceDto.setContentMessage("Message");
        sendInvoiceDto.setCustomerName("Amit");
        sendInvoiceDto.setSubject("Invoice");
        sendInvoiceDto.setTo("verma1910@gmail.com");
        sendInvoiceDto.setFile(new File("src/test/java/com/ecommerce/" +
                "ecommApp/invoice/invoiceSend/service/SendInvoiceServiceTest.java"));
        return sendInvoiceDto;
    }

    public ViewProductDto getViewProductDto() {

        ViewProductDto viewProductDto = new ViewProductDto();
        viewProductDto.setCustomerId(1L);
        viewProductDto.setProductId(1L);
        return viewProductDto;
    }

    public OrderDetails getOrderDetails() {
        OrderDetails orderDetails = new OrderDetails();
        List<String> list = new ArrayList<>();
        list.add("Cash");
        orderDetails.setOrderID("1");
        orderDetails.setProductName("t-shirt");
        orderDetails.setQuantity(1);
        orderDetails.setTotalCost(500F);
        orderDetails.setCustomerDto(getCustomerDto());
        orderDetails.setMode(list);
        return orderDetails;
    }
}
