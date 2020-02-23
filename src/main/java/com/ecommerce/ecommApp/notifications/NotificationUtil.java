package com.ecommerce.ecommApp.notifications;

import com.ecommerce.ecommApp.EcommAppApplication;
import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.notifications.handlers.NotificationHandler;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Properties;

/**
 * This class will work as a Utility Class for the Notification Package.
 * Some of the most Important functions are added.
 */
public class NotificationUtil {

    private static final Logger log = LoggerFactory.getLogger(NotificationUtil.class);
    public static Environment environment = EcommAppApplication.environment;
    private static NotificationHandler handler;

    static {
        handler = new NotificationHandler();
    }

    /**
     * @return : This Method will return Properties object which will contain wall the basic required configurations for the kafka topic consumer.
     */
    public static Properties getConsumerConfigs() {
        log.trace("Preparing Consumer Configurations");
        Properties props = new Properties();
        try {
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty
                    (CommonsUtil.KAFKA_BOOTSTRAP_SERVERS));
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                    StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                    StringDeserializer.class.getName());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaNotificationService");
            log.trace("Returning Consumer Configs");
        } catch (Exception ex) {
            log.error("Error in Creating Consumer Configurations");
        }
        return props;
    }

    /**
     * This method will return a Kafka Comsumer Object
     * @param props : Kafka Consumer Properties
     * @param kafkaTopicName : Kafka Topic Name
     */
    public static KafkaConsumer createConsumer(Properties props, String kafkaTopicName) {
        KafkaConsumer kafkaConsumer = new KafkaConsumer<Long, String>(props);
        kafkaConsumer.subscribe(Collections.singletonList(kafkaTopicName));
        log.trace("Consumer Created for Topic {}",kafkaTopicName);
        return kafkaConsumer;
    }

    public static String getApplicationProperty(String key) {
        return environment.getRequiredProperty(key);
    }

    /**
     * @return : Notification Handler Object.
     */
    public static NotificationHandler getNotificationHandler() {
        return handler;
    }

    /**
     * Nested Class which will contain the basic Message Template which we will send to the User.
     */
    public static class MessageTemplate {
        public final static String USER_REGISTERED_MESSAGE = "Your Account with CustomerDto ID %s on " + getApplicationProperty("info.app.name")
                + " is successfully registered";

        public final static String ORDER_PLACED_MESSAGE = "Your order for %s %S Product is successfully placed having a Order Id %s ";

        public final static String ORDER_CANCELLED_MESSAGE = "Your order for %s  %S Product is CANCELLED having a Order Id %s ";

        public final static String ORDER_STATUS_MESSAGE = "Your order status for %s Quandity %S Product is now %s ";
    }
}
