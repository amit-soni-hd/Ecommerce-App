package com.ecommerce.ecommApp.notifications.services;

import com.ecommerce.ecommApp.notifications.NotificationUtil;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

/**
 * This Thread will work as a Consumer for Topic Order_Status.
 * This will continously poll the data from the topic and process each record and send that
 * Processed Object to the notification handler.
 */
public class OrderStatusService extends Thread
{
    String kafkaTopicName;
    Properties props;
    KafkaConsumer kafkaConsumer;

    public OrderStatusService(String kafkaTopicName)
    {
        super();
        this.kafkaTopicName=kafkaTopicName;
    }

    /**
     * This Run method will work as a poll. This method will continously poll records from the Kafka topic
     */
    @Override
    public void run() {
        super.run();
        props = NotificationUtil.getConsumerConfigs();
        kafkaConsumer=NotificationUtil.createConsumer(props,kafkaTopicName);
        while(true)
        {
            final ConsumerRecords<Long, String> consumerRecords =
                    kafkaConsumer.poll(100);
            consumerRecords.forEach(record -> {
                // TODO Integrate Order Status service
                final String payload=record.value();
            });
        }
    }
}
