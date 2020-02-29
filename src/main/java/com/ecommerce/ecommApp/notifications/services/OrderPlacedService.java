package com.ecommerce.ecommApp.notifications.services;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderDetails;
import com.ecommerce.ecommApp.notifications.NotificationUtil;
import com.ecommerce.ecommApp.notifications.handlers.NotificationHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * This Thread will work as a Consumer for Topic Order_Placed.
 * This will continously poll the data from the topic and process each record and send that
 * Processed Object to the notification handler.
 */
public class OrderPlacedService extends Thread {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacedService.class);
    String kafkaTopicName;
    Properties props;
    KafkaConsumer kafkaConsumer;
    ObjectMapper objectMapper;
    NotificationHandler notificationHandler;

    public OrderPlacedService(String kafkaTopicName) {
        super();
        this.kafkaTopicName = kafkaTopicName;
    }

    /**
     * This Run method will work as a poll. This method will continously poll records from the Kafka topic
     */
    @Override
    public void run() {
        super.run();
        props = NotificationUtil.getConsumerConfigs();
        kafkaConsumer = NotificationUtil.createConsumer(props, kafkaTopicName);
        objectMapper = CommonsUtil.getObjectMapper();
        notificationHandler = NotificationUtil.getNotificationHandler();

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    kafkaConsumer.poll(100);
            consumerRecords.forEach(record -> {
                // TODO use the twilio sdk
                final String json = record.value();
                try {
                    OrderDetails orderPlaced = objectMapper.readValue(json, OrderDetails.class);
                    log.trace("Record Found : {}", orderPlaced.toString());
                    String message = formatMessage(orderPlaced);
                    notificationHandler.sendNotification(getName(), orderPlaced.getMode(), orderPlaced, message);
                } catch (IOException ex) {
                    log.error("Error in processing Record : {}", record);
                }
            });
        }
    }

    /**
     * @return : This Method will format the text Message which we will be sending to the User via different modes.
     */
    private String formatMessage(OrderDetails orderPlaced) {
        return String.format(NotificationUtil.MessageTemplate.ORDER_PLACED_MESSAGE, orderPlaced.getQuantity(), orderPlaced.getProductName(), orderPlaced.getOrderID());
    }
}
