package com.ecommerce.ecommApp.kafka.consumer;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Slf4j
@Component
public class Consumer {

    @Autowired
    private Environment environment;

    // get the consumer properties
    private Properties getProperties(String groupId) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty(CommonsUtil.KAFKA_BOOTSTRAP_SERVERS));
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    public KafkaConsumer getKafkaConsumer(String groupId) {
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(getProperties(groupId));
        return kafkaConsumer;
    }

    public void closeConsumer(KafkaConsumer<String, String> kafkaConsumer) {
        kafkaConsumer.close();
    }

    // subscribe the topic and get the data
    public void printData(KafkaConsumer kafkaConsumer, String topic) {

        // subscribe the topic
        kafkaConsumer.subscribe(Arrays.asList(topic));

        // pool the topic
        while(true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(10));
            log.info("Consume record : " + consumerRecords.count());
            if(consumerRecords.count() > 0) {
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    log.info("\n\n\n");
                    log.info("Key : " + consumerRecord.key() + "\nvalues : " + consumerRecord.value());
                    log.info("\nPartition : " + consumerRecord.partition() + "\nOffset : " + consumerRecord.offset());
                }
            }

        }
    }
}
