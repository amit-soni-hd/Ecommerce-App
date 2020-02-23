package com.ecommerce.ecommApp.kafka.consumer;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.kafka.consumer.Consumer;
import com.ecommerce.ecommApp.view.dto.ViewProductDto;
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

    public List<ViewProductDto> getViewProduct(String customerId) {

        objectMapper = CommonsUtil.getObjectMapper();
        List<ViewProductDto> list;
        kafkaConsumer = consumer.getKafkaConsumer(UUID.randomUUID().toString());
        list = fetchViewProduct(kafkaConsumer, environment.getProperty(CommonsUtil.VIEW_PRODUCT_TOPIC), customerId);

        return list;
    }

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
                        if(viewProductDto.getCustomerId().equals(customerId))
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
