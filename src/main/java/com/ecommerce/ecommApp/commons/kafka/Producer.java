package com.ecommerce.ecommApp.commons.kafka;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.UUID;

@Component
public class Producer {

    @Autowired
    private Environment environment;

    public Properties getProducerConfigs() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getRequiredProperty(CommonsUtil.KAFKA_BOOTSTRAP_SERVERS));
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"kafka.bootstrap.server");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "id : " + UUID.randomUUID().toString());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public KafkaProducer<String, String> getKafkaProducer(Properties properties) {
        return new KafkaProducer<>(properties);
    }

    public void producerRecord(String json, String kafkaTopicName, KafkaProducer<String, String> kafkaProducer) {
        ProducerRecord record = new ProducerRecord<>(kafkaTopicName, null, json);
        kafkaProducer.send(record);
//        kafkaProducer.flush();
    }

    public void closeProducer(KafkaProducer<String, String> kafkaProducer) {
        kafkaProducer.flush();
        kafkaProducer.close();
    }

}
