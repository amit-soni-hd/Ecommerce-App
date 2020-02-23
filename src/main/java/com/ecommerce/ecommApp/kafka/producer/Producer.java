package com.ecommerce.ecommApp.kafka.producer;

import com.ecommerce.ecommApp.EcommAppApplication;
import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class Producer {

    public void produceViewProduct(String json, String kafkaTopicName) {
        Properties props = getProducerConfigs();
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
        ProducerRecord record = new ProducerRecord<>(kafkaTopicName, null, json);
        kafkaProducer.send(record);
        kafkaProducer.flush();
        kafkaProducer.close();
    }

    private Properties getProducerConfigs() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, EcommAppApplication.environment.getRequiredProperty(CommonsUtil.KAFKA_BOOTSTRAP_SERVERS));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "id : " + System.currentTimeMillis() + 1);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }
}
