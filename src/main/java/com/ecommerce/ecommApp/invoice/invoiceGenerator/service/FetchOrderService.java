package com.ecommerce.ecommApp.invoice.invoiceGenerator.service;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.kafka.Consumer;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderDetails;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.pdfUtils.Utils;
import com.ecommerce.ecommApp.orders.services.OrderServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.Response;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Service
public class FetchOrderService {

    private Consumer consumer;
    private KafkaConsumer kafkaConsumer;
    private Environment environment;
    private ObjectMapper objectMapper;
    private OrderServices orderServices;
    private InvoiceGeneratorService invoiceGeneratorService;

    /**
     * constructor for initialize the local variable
     * @param consumer provide KafkaConsumer
     * @param environment for access the application properties value
     * @param orderServices for getting the details of user order
     * @param invoiceGeneratorService for generate the invoice
     */
    @Autowired
    public FetchOrderService(Consumer consumer, Environment environment, OrderServices orderServices,
                             InvoiceGeneratorService invoiceGeneratorService) {

        this.consumer = consumer;
        this.environment = environment;
        this.objectMapper = CommonsUtil.getObjectMapper();
        this.orderServices = orderServices;
        this.invoiceGeneratorService = invoiceGeneratorService;
    }

    /**
     * method for fetching the record from order placed topic
     * then convert to OrdersDto which contain the details of order
     * call the invoiceGenerate method of invoiceGenerateService for generate the invoice
     */
    public void fetchOrder() {

        Properties properties = consumer.getProperties(Utils.GROUP_ID);
        this.kafkaConsumer = consumer.getKafkaConsumer(properties);

        log.info("Start the fetch service for consume the record from topic");
        kafkaConsumer.subscribe(Collections.singleton(
                environment.getProperty(CommonsUtil.NOTIFICATION_ORDER_PLACED_TOPIC)));


        while (true) {

            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(5));

            if (records.count() > 0) {
                records.forEach(record -> {
                    try {


                        OrderDetails orderDetails = objectMapper.readValue(record.value(), OrderDetails.class);
                        OrdersDTO ordersDTO = orderServices.getOrderDetails(orderDetails.getOrderID());
                            invoiceGeneratorService.invoiceGenerate(ordersDTO);
                        log.info("Invoice send for customerId {} with productId {}", ordersDTO.getCustomerID(),
                                ordersDTO.getProductID());

                    } catch (JsonProcessingException e) {

                        log.error("JsonProcessingError : " + e.getCause());
                        e.printStackTrace();

                    } catch (NotFoundException e) {

                        log.error("Order not found : " + e.getCause() + "\n Message : " + e.getMessage());
                        e.printStackTrace();
                    }

                });
            }
        }
    }

}
