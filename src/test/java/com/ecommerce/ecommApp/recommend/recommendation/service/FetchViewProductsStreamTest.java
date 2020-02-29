package com.ecommerce.ecommApp.recommend.recommendation.service;

import com.ecommerce.ecommApp.Objects;
import com.ecommerce.ecommApp.recommend.view.dto.ViewProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FetchViewProductsStreamTest {

    @InjectMocks
    private FetchViewProductsStream fetchViewProductsStream;

    @Mock
    private Environment environment;

    private Topology topology;
    private TopologyTestDriver testDriver;
    private List<Long> productIds;
    private String INPUT_TOPIC;
    private ConsumerRecordFactory<Long, String> factory;
    private Objects objects;
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        this.productIds = new ArrayList<>();
        this.objects = new Objects();
        this.objectMapper = new ObjectMapper();

        this.INPUT_TOPIC = "view_product_stream_input";
        Properties properties = getStreamProperties();

        when(environment.getProperty(anyString())).thenReturn(INPUT_TOPIC);

        this.topology = fetchViewProductsStream.createTopology(productIds, 1L);
        this.testDriver = new TopologyTestDriver(topology, properties);

        this.factory = new ConsumerRecordFactory<>(INPUT_TOPIC, new LongSerializer(), new StringSerializer());

    }

    @After
    public void close() {
        testDriver.close();
        productIds.clear();
    }

    @Test
    public void testFetchViewProductStream() throws JsonProcessingException {

        ViewProductDto viewProductDto = objects.getViewProductDto();

        testDriver.pipeInput(factory.create(objectMapper.writeValueAsString(objects.getViewProductDto())));
        testDriver.pipeInput(factory.create(objectMapper.writeValueAsString(objects.getViewProductDto())));
        testDriver.pipeInput(factory.create(objectMapper.writeValueAsString(objects.getViewProductDto())));
        testDriver.pipeInput(factory.create(objectMapper.writeValueAsString(objects.getViewProductDto())));

        assertEquals(viewProductDto.getProductId(), productIds.get(0));
        assertTrue(productIds.size() == 4);
    }

    private Properties getStreamProperties() {

        Properties properties = new Properties();
        String applicationId = UUID.randomUUID().toString();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummyLocalhost:1234");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.LongSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }

}
