package com.ecommerce.ecommApp.kafka;

import com.ecommerce.ecommApp.commons.kafka.Producer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
@EmbeddedKafka(partitions = 1, topics = {"testTopic"})
public class ProducerTest {

    private static String TEST_TOPIC = "testTopic";;
    private Producer producer;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule =
            new EmbeddedKafkaRule(1, true, TEST_TOPIC);

    @Before
    public void setUp() {
        producer = new Producer();
    }


    @Test
    public void testKafkaProducer() {
        Consumer<String, String> consumer = configureConsumer();
        KafkaProducer<String, String> kafkaProducer = configureProducer();

        producer.producerRecord("my-test-value", TEST_TOPIC, kafkaProducer);

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, TEST_TOPIC);
        assertNotNull(singleRecord);
        assertEquals("my-test-value", singleRecord.value());

        consumer.close();
        producer.closeProducer(kafkaProducer);
    }

    private Consumer<String, String> configureConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaRule.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps)
                .createConsumer();
        consumer.subscribe(Collections.singleton(TEST_TOPIC));
        return consumer;
    }

    private KafkaProducer<String, String> configureProducer() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaRule.getEmbeddedKafka()));
        Properties properties = new Properties();
        properties.putAll(producerProps);
        return producer.getKafkaProducer(properties);
    }
}

