package com.ecommerce.ecommApp.invoice.invoiceGenrator.converter;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.commons.exceptions.ElementNotFoundException;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.converter.OrderDtoToInvoiceFormat;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.dto.InvoiceFormatDto;
import com.ecommerce.ecommApp.products.services.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderDtoToInvoiceFormatTest {

    @InjectMocks
    private OrderDtoToInvoiceFormat orderDtoToInvoiceFormat;

    @Mock
    private ProductService productService;

    private Objects objects;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.objects = new Objects();
    }

    @Test
    public void convertToInvoiceFormatDtoTest() throws ElementNotFoundException, InterruptedException {

        OrdersDTO ordersDTO = objects.getOrderDto();
        Product product = objects.getProduct();

        when(productService.getProduct(anyLong())).thenReturn(product);
        InvoiceFormatDto invoiceFormatDto = orderDtoToInvoiceFormat.convertToInvoiceFormatDto(ordersDTO);

        assertEquals(product.getBrand(), invoiceFormatDto.getInvoiceDetails().getBrand());
        assertEquals(ordersDTO.getOrderID(), invoiceFormatDto.getInvoiceDetails().getOrderId());

    }

    @Test(expected = RuntimeException.class)
    public void productNotFountException() throws ElementNotFoundException, InterruptedException {

        OrdersDTO ordersDTO = objects.getOrderDto();

        when(productService.getProduct(anyLong())).thenThrow(ElementNotFoundException.class);
        orderDtoToInvoiceFormat.convertToInvoiceFormatDto(ordersDTO);
    }
}
