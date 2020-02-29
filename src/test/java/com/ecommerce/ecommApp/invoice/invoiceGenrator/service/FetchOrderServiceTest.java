package com.ecommerce.ecommApp.invoice.invoiceGenrator.service;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.commons.kafka.Consumer;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderDetails;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.service.FetchOrderService;
import com.ecommerce.ecommApp.invoice.invoiceGenerator.service.InvoiceGeneratorService;
import com.ecommerce.ecommApp.orders.services.OrderServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.timeout.TimeoutException;
import javassist.NotFoundException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.*;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestTimedOutException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(BlockJUnit4ClassRunner.class)
public class FetchOrderServiceTest {

    @InjectMocks
    private FetchOrderService fetchOrderService;

    @Mock
    private InvoiceGeneratorService invoiceGeneratorService;

    @Mock
    private Environment environment;

    @Mock
    private OrderServices orderServices;

    @Mock
    private Consumer consumer;

    private Producer<String, String> producer;
    private static final String TEST_TOPIC = "testTopic";
    private Objects objects;
    private ObjectMapper objectMapper;
    private static final int MIN_TIMEOUT = 5000;


    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule =
            new EmbeddedKafkaRule(1, true, TEST_TOPIC);

    @SuppressWarnings("deprecation")
    @Rule
    public Timeout timeout = new Timeout(MIN_TIMEOUT, TimeUnit.MILLISECONDS) {
        public Statement apply(Statement base, Description description) {
            return new FailOnTimeout(base, MIN_TIMEOUT) {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        super.evaluate();
                        throw new RuntimeException("");
                    } catch (Exception e) {}
                }
            };
        }
    };


    @Before
    public void setUp() throws JsonProcessingException {
        MockitoAnnotations.initMocks(this);
        this.objects = new Objects();
        this.objectMapper = new ObjectMapper();
        OrderDetails orderDetails = objects.getOrderDetails();
        this.producer = configureProducer();
        producer.send(new ProducerRecord<>(TEST_TOPIC, "123", objectMapper.writeValueAsString(orderDetails)));
        when(consumer.getKafkaConsumer(any())).thenReturn(getKafkaConsumer());

    }

    @After
    public void close() {
        this.producer.close();
    }

    @Test(expected = RuntimeException.class)
    public void testFetchOrders() throws NotFoundException, TimeoutException {

        OrdersDTO ordersDTO = objects.getOrderDto();
        when(environment.getProperty(anyString())).thenReturn(TEST_TOPIC);
        when(consumer.getProperties(anyString())).thenReturn(mock(Properties.class));
        when(orderServices.getOrderDetails(any())).thenReturn(ordersDTO);
        fetchOrderService.fetchOrder();

    }


    private KafkaConsumer<String, String> getKafkaConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaRule.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Properties properties = new Properties();
        properties.putAll(consumerProps);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        return consumer;
    }

    private Producer<String, String> configureProducer() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaRule.getEmbeddedKafka()));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new DefaultKafkaProducerFactory<String, String>(producerProps).createProducer();
    }

}

