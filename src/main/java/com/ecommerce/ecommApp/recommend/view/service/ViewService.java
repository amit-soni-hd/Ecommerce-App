package com.ecommerce.ecommApp.recommend.view.service;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.kafka.Producer;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;
import com.ecommerce.ecommApp.recommend.view.dto.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Properties;


@Slf4j
@Service
public class ViewService {

    private Producer producer;
    private String viewTopic;
    private ObjectMapper objectMapper;
    private Environment environment;

    /**
     * constructor for initialize the loca variable
     * @param producer object of producer which provide the kafka producer
     * @param environment Object of environment for access the value of application properties
     */
    @Autowired
    public ViewService(Producer producer, Environment environment) {
        this.environment = environment;
        this.producer = producer;
        this.viewTopic = environment.getProperty(CommonsUtil.VIEW_PRODUCT_TOPIC);
        this.objectMapper = CommonsUtil.getObjectMapper();
    }

    /**
     * method for produce the view product
     * @param viewProductDto contains the details of product and user
     * @return object of ApiResponse which contain the status and message
     */
    public ApiResponse viewProduct(ViewProductDto viewProductDto) {

        try {

            String data = objectMapper.writeValueAsString(viewProductDto);
            Properties properties = producer.getProducerConfigs();
            KafkaProducer kafkaProducer = producer.getKafkaProducer(properties);
            producer.producerRecord(data, viewTopic, kafkaProducer);

        } catch (JsonProcessingException e) {

            log.error("JsonProcessingException : " + e.getMessage());
            throw new RuntimeException("Object convert exception  " + e.getOriginalMessage());

        }
        return new ApiResponse(HttpStatus.OK, "Saved view product");
    }
}
