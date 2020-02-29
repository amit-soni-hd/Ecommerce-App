package com.ecommerce.ecommApp.recommend.view.service;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.kafka.Consumer;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
public class FetchViewedProduct {

    @Autowired
    private Environment environment;

    @Autowired
    private Consumer consumer;

    private KafkaConsumer<String, String> kafkaConsumer;
    private ObjectMapper objectMapper;

    /**
     * method for getting the list of view product
     * initialize the kafkaConsumer variable for fetching the record from topic
     * @param customerId used to fetch the record of particular user
     * @return list of ViewProductDto which contains the products
     */
    public List<ViewProductDto> getViewProduct(String customerId) {

        objectMapper = CommonsUtil.getObjectMapper();
        List<ViewProductDto> list;
        Properties properties = consumer.getProperties(UUID.randomUUID().toString());
        kafkaConsumer = consumer.getKafkaConsumer(properties);
        list = fetchViewProduct(kafkaConsumer, environment.getProperty(CommonsUtil.VIEW_PRODUCT_TOPIC), customerId);

        return list;
    }

    /**
     * fetch the record from topic and convert to ViewProductDto
     * loop will be break after consume the all record from topic
     * loop variable used to wait for some interaction before break
     * @param kafkaConsumer used to consume the record from topic
     * @param topic which topic we have to consume the record
     * @param customerId used to separate the record of user
     * @return list of ViewProductDto which contains the products details
     */
    private List<ViewProductDto> fetchViewProduct(KafkaConsumer kafkaConsumer, String topic, String customerId) {

        // subscribe the topic
        kafkaConsumer.subscribe(Arrays.asList(topic));
        List<ViewProductDto> list = new ArrayList<>();
        Integer loop = 50;

        // pool the topic
        while(true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
            log.info("Consume record : " + consumerRecords.count());
            if(consumerRecords.count() > 0) {
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    log.info("Key : " + consumerRecord.key() + "\nvalues : " + consumerRecord.value());
                    try {
                        ViewProductDto viewProductDto = objectMapper.readValue(consumerRecord.value(), ViewProductDto.class);
                        if(viewProductDto.getCustomerId().toString().equals(customerId))
                            list.add(viewProductDto);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            } else if(loop-- < 0)
                break;
            }
        return list;
    }

}
