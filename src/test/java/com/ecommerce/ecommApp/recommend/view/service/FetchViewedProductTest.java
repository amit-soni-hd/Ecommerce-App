package com.ecommerce.ecommApp.recommend.view.service;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.commons.kafka.Consumer;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@EmbeddedKafka(partitions = 1, topics = {"view_topic"})
public class FetchViewedProductTest {

    @InjectMocks
    private FetchViewedProduct fetchViewedProduct;

    @Mock
    private Consumer consumer;

    @Mock
    private Environment environment;

    private static final String TEST_TOPIC = "view_topic";
    private Objects objects;
    private ObjectMapper objectMapper;
    private Properties properties;
    private KafkaConsumer<String, String> kafkaConsumer;
    private Producer<String, String> producer;
    private ViewProductDto viewProductDto;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule =
            new EmbeddedKafkaRule(1, true, TEST_TOPIC);

    @Before
    public void setUp() throws JsonProcessingException {

        MockitoAnnotations.initMocks(this);

        this.objects = new Objects();
        this.objectMapper = new ObjectMapper();
        this.properties = getConsumerProperties();
        this.kafkaConsumer = configureConsumer(properties);
        this.producer = configureProducer();

        viewProductDto = objects.getViewProductDto();
        producer.send(new ProducerRecord<>(TEST_TOPIC, "123", objectMapper.writeValueAsString(viewProductDto)));
    }

    @After
    public void close() {
        kafkaConsumer.close();
        producer.close();
    }


    @Test
    public void testKafkaConsumer() {

        when(environment.getProperty(anyString())).thenReturn(TEST_TOPIC);
        when(consumer.getProperties(anyString())).thenReturn(properties);
        when(consumer.getKafkaConsumer(any())).thenReturn(kafkaConsumer);
        List<ViewProductDto> viewProduct = fetchViewedProduct.getViewProduct("1");

        assertTrue(viewProduct.size() > 0);
        assertEquals(viewProductDto.getProductId(), viewProduct.get(0).getProductId());

    }


    private Properties getConsumerProperties() {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaRule.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Properties properties = new Properties();
        properties.putAll(consumerProps);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return properties;
    }

    private KafkaConsumer<String, String> configureConsumer(Properties properties) {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(TEST_TOPIC));
        return consumer;
    }

    private Producer<String, String> configureProducer() {

        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaRule.getEmbeddedKafka()));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new DefaultKafkaProducerFactory<String, String>(producerProps).createProducer();
    }
}
