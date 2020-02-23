package com.ecommerce.ecommApp.invoiceGenerator.service;

import com.ecommerce.ecommApp.invoiceGenerator.dto.InvoiceRequestDto;
import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderPlaced;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.kafka.consumer.Consumer;
import com.ecommerce.ecommApp.orders.services.OrderServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FetchOrderService {

    private Consumer consumer;
    private KafkaConsumer kafkaConsumer;
    private Environment environment;
    private ObjectMapper objectMapper;
    private OrderServices orderServices;

    @Autowired
    public FetchOrderService(Consumer consumer, Environment environment, OrderServices orderServices) {

        this.consumer = consumer;
        this.environment = environment;
        this.kafkaConsumer = consumer.getKafkaConsumer(UUID.randomUUID().toString());
        this.objectMapper = CommonsUtil.getObjectMapper();
        this.orderServices = orderServices;
    }

    public List<OrdersDTO> fetchOrder(InvoiceRequestDto invoiceRequestDto) {

        kafkaConsumer.subscribe(Collections.singleton(
                environment.getProperty(CommonsUtil.NOTIFICATION_ORDER_PLACED_TOPIC)));

        Integer loop = 40;
        List<OrdersDTO> ordersList = new ArrayList<>();

        while(true) {

            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(10));
            log.info("Fetch {} orders\n" , records.count());

            if(records.count() > 0) {
                records.forEach( record -> {
                    try {
                            OrderPlaced orderPlaced = objectMapper.readValue(record.value(), OrderPlaced.class);
                            OrdersDTO ordersDTO = orderServices.getOrderDetails(orderPlaced.getOrderID());

                        if(invoiceRequestDto.getCustomerId().equals(ordersDTO.getCustomerID()) &&
                                invoiceRequestDto.getProductIds().contains(ordersDTO.getProductID())) {
                            ordersList.add(ordersDTO);
                        }
                    } catch (JsonProcessingException e) {

                        log.error("JsonProcessingError : " + e.getCause());
                        e.printStackTrace();

                    } catch (NotFoundException e) {

                        log.error("Order not found : " + e.getCause() + "\n Message : " + e.getMessage());
                        e.printStackTrace();
                    }

                });
            } else if(loop-- < 1)
                break;
        }
        this.consumer.closeConsumer(kafkaConsumer);
        return ordersList;
    }
}
