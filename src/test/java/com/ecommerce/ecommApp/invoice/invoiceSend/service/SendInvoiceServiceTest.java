package com.ecommerce.ecommApp.invoice.invoiceSend.service;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.invoice.invoiceSend.dto.SendInvoiceDto;
import com.sendgrid.Response;
import com.sendgrid.SendGridAPI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SendInvoiceServiceTest {

    @InjectMocks
    private SendInvoiceService sendInvoiceService;

    @Mock
    private SendGridAPI sendGridAPI;

    private Objects objects;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.objects = new Objects();

    }

    @Test(expected = RuntimeException.class)
    public void sendInvoiceExceptionTest() {

        SendInvoiceDto sendInvoiceDto = objects.getSendInvoiceDto();
        sendInvoiceDto.setFile(new File("wrong path"));
        sendInvoiceService.sendInvoice(sendInvoiceDto);
    }

    @Test
    public void sendInvoiceTest() throws IOException {

        Response response = new Response();
        response.setStatusCode(200);
        SendInvoiceDto sendInvoiceDto = objects.getSendInvoiceDto();

        when(sendGridAPI.api(any())).thenReturn(response);
        Response result = sendInvoiceService.sendInvoice(sendInvoiceDto);

        verify(sendGridAPI, times(1)).api(any());
        assertEquals(response.getStatusCode(), result.getStatusCode());

    }
}
