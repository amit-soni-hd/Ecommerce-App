package com.ecommerce.ecommApp.recommend.recommendation.service;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@Service
public class FetchViewProductsStream {

    private Environment environment;
    private ObjectMapper objectMapper;

    /**
     * constructor used to initialize the local variable
     *
     * @param environment used to access the application properties value
     */
    @Autowired
    public FetchViewProductsStream(Environment environment) {
        this.objectMapper = CommonsUtil.getObjectMapper();
        this.environment = environment;
    }

    /**
     * used to get the properties for kafka stream
     *
     * @return object of properties
     */
    private Properties getStreamProperties() {
        Properties properties = new Properties();

        String applicationId = UUID.randomUUID().toString();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty(CommonsUtil.KAFKA_BOOTSTRAP_SERVERS));
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.LongSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

        return properties;
    }

    /**
     * used to start the stream process
     *
     * @param customerId for filter the view product for same user
     * @return list of  ids of view product
     */
    public List<Long> start(Long customerId) {
        List<Long> viewProducts = new ArrayList<>();
        Properties properties = getStreamProperties();
        KafkaStreams kafkaStreams = new KafkaStreams(createTopology(viewProducts, customerId), properties);

        kafkaStreams.cleanUp();
        kafkaStreams.start();

        try {
            Thread.sleep(7000);
            kafkaStreams.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

        return viewProducts;
    }

    /**
     * create the topology for running the stream process
     *
     * @param list       list of view products id
     * @param customerId user id for filter the user record
     * @return kafka stream object for start the topology
     */
    public Topology createTopology(List<Long> list, Long customerId) {

        StreamsBuilder builder = new StreamsBuilder();
        Serdes.StringSerde stringSerde = new Serdes.StringSerde();
        Serdes.LongSerde longSerde = new Serdes.LongSerde();

        builder
                .stream(environment.getProperty(CommonsUtil.VIEW_PRODUCT_TOPIC), Consumed.with(longSerde, stringSerde))
                .filter((key, value) -> isValidView(value, customerId))
                .selectKey((key, value) -> selectKey(value))
                .groupByKey()
                .count()
                .toStream()
                .foreach((productId, value) -> list.add(productId));

        return builder.build();
    }

    /**
     * get the key from record
     *
     * @param value view product record
     * @return product id
     */
    private Long selectKey(String value) {
        try {
            ViewProductDto viewProductDto = objectMapper.readValue(value, ViewProductDto.class);
            return viewProductDto.getProductId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JsonProcessingException : " + e.getOriginalMessage());
        }
    }

    /**
     * used to validate the view for user
     *
     * @param value      view product
     * @param customerId user id for filter the view product
     * @return return true if view is valid otherwise false
     */
    private Boolean isValidView(String value, Long customerId) {
        try {
            ViewProductDto viewProductDto = objectMapper.readValue(value, ViewProductDto.class);
            if (viewProductDto.getCustomerId().equals(customerId)) {
                return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json processing exception " + e.getOriginalMessage() + "\nCause " + e.getCause());
        }
    }
}
