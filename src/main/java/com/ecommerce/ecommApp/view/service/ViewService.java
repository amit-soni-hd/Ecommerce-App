package com.ecommerce.ecommApp.view.service;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.view.dto.ViewProductDto;
import com.ecommerce.ecommApp.kafka.producer.Producer;
import com.ecommerce.ecommApp.view.dto.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ViewService {

    private Producer producer;
    private String viewTopic;
    private ObjectMapper objectMapper;
    private Environment environment;

    @Autowired
    public ViewService(Producer producer, Environment environment) {
        this.environment = environment;
        this.producer = producer;
        this.viewTopic = environment.getProperty(CommonsUtil.VIEW_PRODUCT_TOPIC);
        this.objectMapper = CommonsUtil.getObjectMapper();
    }

    public ApiResponse viewProduct(ViewProductDto viewProductDto) {

        try {
            String data = objectMapper.writeValueAsString(viewProductDto);
            producer.produceViewProduct(data, viewTopic);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException : " + e.getMessage());
            throw new RuntimeException("Object convert exception  " + e.getOriginalMessage());
        } catch (Exception e) {
            log.error("Exception" + e.getMessage());
            throw new RuntimeException("Unknown exception " + e.getMessage() + "\nCause " + e.getCause());
        }
        return new ApiResponse(HttpStatus.OK, "Saved view product");
    }
}
