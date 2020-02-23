package com.ecommerce.ecommApp.commons.Util;

import com.ecommerce.ecommApp.commons.NotificationProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonsUtil {

    private static final ObjectMapper objectMapper;
    private static final NotificationProducer notificationProducer;
    public static final String SERVER_PORT="server.port";
    public static final String NOTIFICATION_USER_REGISTERED_SERVICE = "User Registered";
    public static final String NOTIFICATION_ORDER_PLACED_SERVICE = "Order Placed";
    public static final String NOTIFICATION_ORDER_STATUS_SERVICE = "Order Status";
    public static final String NOTIFICATION_ORDER_CANCELLED_SERVICE = "Order Cancelled";
    public static final String TWILIO_ASSIGNED_NUMBER = "twilio.number";
    public static final String SENDGRID_FROM_EMAIL = "sendgrid.email.from";
    public static final String SENDGRID_ACCESS_TOKEN="sendgrid.access.token";
    public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.server";

    public static final String NOTIFICATION_REGISTERED_TOPIC = "notification.registered.topic";
    public static final String NOTIFICATION_ORDER_PLACED_TOPIC = "notification.order.placed.topic";
    public static final String NOTIFICATION_ORDER_STATUS_TOPIC = "notification.order.status.topic";
    public static final String NOTIFICATION_ORDER_CANCEL_TOPIC = "notification.order.cancel.topic";

    public static final String VIEW_PRODUCT_TOPIC = "view.product.topic";

    public static final String EMAIL_EXISTS = "There is an account with this email address:";
    public static final String CUSTOMER_NOT_FOUND = "Customer Not Found";
    public static final String WRONG_CUSTOMER_ID = "Wrong Customer Id";

    static {
        notificationProducer = new NotificationProducer();
        objectMapper = new ObjectMapper();
    }

    public static NotificationProducer getNotificationProducer() {
        return notificationProducer;
    }

    public static ObjectMapper getObjectMapper() { return objectMapper; }
}
