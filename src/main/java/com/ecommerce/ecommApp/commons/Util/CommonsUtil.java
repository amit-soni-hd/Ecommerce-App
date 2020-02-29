package com.ecommerce.ecommApp.commons.Util;

import com.ecommerce.ecommApp.commons.kafka.Producer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonsUtil {

    private static final ObjectMapper objectMapper;
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

    // endpoint for invoice generator
    public static final String INVOICE = "/invoice";
    public static final String INVOICE_GENERATE = "/generate";

    // endpoint for invoice send
    public static final String INVOICE_SEND = "/send";

    // endpoint for recommendation
    public static final String RECOMMENDATION = "/recommendation";
    public static final String FETCH_RECOMMENDATION = "/fetch";

    // endpoint for view product
    public static final String VIEWED = "/viewed";
    public static final String VIEW_PRODUCT = "/product";

    public static final String JWT_SECRET = "app.jwtSecret";
    public static final String JWT_EXPIRATION_TIME = "app.jwtExpirationInMs";

    static {
        objectMapper = new ObjectMapper();
    }

    public static ObjectMapper getObjectMapper() { return objectMapper; }
}
