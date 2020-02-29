package com.ecommerce.ecommApp.kafka;

import com.ecommerce.ecommApp.commons.kafka.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConsumerTest {

    private static final String TEST_TOPIC = "testTopic";
    private Consumer consumer;


    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule =
            new EmbeddedKafkaRule(1, true, TEST_TOPIC);

    @Before
    public void setUp() {
        this.consumer = new Consumer();
    }

    @Test
    public void testKafkaConsumer() {

        KafkaConsumer<String, String> kafkaConsumer = configureConsumer();
        Producer<String, String> producer = configureProducer();

        producer.send(new ProducerRecord<>(TEST_TOPIC, "123", "my-test-value"));

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(kafkaConsumer, TEST_TOPIC);
        assertNotNull(singleRecord);
        assertEquals("my-test-value", singleRecord.value());

        consumer.closeConsumer(kafkaConsumer);
        producer.close();
    }

    private KafkaConsumer<String, String> configureConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaRule.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Properties properties = new Properties();
        properties.putAll(consumerProps);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumer = this.consumer.getKafkaConsumer(properties);
        consumer.subscribe(Collections.singleton(TEST_TOPIC));
        return consumer;
    }

    private Producer<String, String> configureProducer() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaRule.getEmbeddedKafka()));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new DefaultKafkaProducerFactory<String, String>(producerProps).createProducer();
    }
}
