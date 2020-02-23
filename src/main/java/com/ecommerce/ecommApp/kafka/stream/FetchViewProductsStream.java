package com.ecommerce.ecommApp.kafka.stream;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.view.dto.ViewProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
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

    @Autowired
    public FetchViewProductsStream(Environment environment) {
        this.objectMapper = CommonsUtil.getObjectMapper();
        this.environment = environment;
    }

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

    public List<Long> start(Long customerId) {
        List<Long> viewProducts = new ArrayList<>();
        Properties properties = getStreamProperties();
        KafkaStreams kafkaStreams = createTopology(properties, viewProducts, customerId);

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

    private KafkaStreams createTopology(Properties properties, List<Long> list, Long customerId) {

        StreamsBuilder builder = new StreamsBuilder();
        Serdes.StringSerde stringSerde = new Serdes.StringSerde();
        Serdes.LongSerde longSerde = new Serdes.LongSerde();
        JsonSerde jsonSerde = new JsonSerde();

        builder
                .stream(environment.getProperty(CommonsUtil.VIEW_PRODUCT_TOPIC), Consumed.with(longSerde, stringSerde))
                .filter((key, value) -> isValidView(value, customerId))
                .selectKey((key, value) -> selectKey(value))
                .groupByKey()
                .count()
                .toStream()
                .foreach( (productId, value) -> list.add(productId));

        return new KafkaStreams(builder.build(), properties);
    }

    private Long selectKey(String  value) {
        try {
            ViewProductDto viewProductDto = objectMapper.readValue(value, ViewProductDto.class);
            return viewProductDto.getProductId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JsonProcessingException : " + e.getOriginalMessage());
        }
    }

    private Boolean isValidView(String value, Long customerId) {
        try {
            ViewProductDto viewProductDto = objectMapper.readValue(value, ViewProductDto.class);
            if( viewProductDto.getCustomerId().equals(customerId)) {
                return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json processing exception " + e.getOriginalMessage() + "\nCause " + e.getCause());
        }
    }
}
