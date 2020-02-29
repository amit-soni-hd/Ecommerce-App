package com.ecommerce.ecommApp.recommend.view.service;

import com.ecommerce.ecommApp.commons.kafka.Producer;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;
import com.ecommerce.ecommApp.recommend.view.dto.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ViewServiceTest {

    @InjectMocks
    private ViewService viewService;

    @Mock
    private Producer producer;

    @Mock
    private Environment environment;

    @Mock
    private ObjectMapper objectMapper;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void viewProductTest() throws JsonProcessingException {

        ViewProductDto viewProductDto = mock(ViewProductDto.class);
        Properties properties = mock(Properties.class);
        KafkaProducer<String, String> kafkaProducer = mock(KafkaProducer.class);

        when(objectMapper.writeValueAsString(any())).thenReturn("hello");
        when(producer.getProducerConfigs()).thenReturn(properties);
        when(producer.getKafkaProducer(any())).thenReturn(kafkaProducer);

        ApiResponse response = viewService.viewProduct(viewProductDto);

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(producer, times(1)).getProducerConfigs();
        verify(producer, times(1)).getKafkaProducer(any());
    }

    @Test(expected = RuntimeException.class)
    public void viewProductExceptionTest() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        viewService.viewProduct(null);
    }

}
