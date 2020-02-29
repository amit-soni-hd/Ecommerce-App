package com.ecommerce.ecommApp.orders.services;

import com.ecommerce.ecommApp.commons.kafka.Producer;
import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.Util.Communication;
import com.ecommerce.ecommApp.commons.enums.NotificationType;
import com.ecommerce.ecommApp.commons.enums.OrderStatus;
import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderDetails;
import com.ecommerce.ecommApp.commons.pojo.orders.ItemsDTO;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Cart;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.commons.security.JwtTokenProvider;
import com.ecommerce.ecommApp.orders.Models.Orders;
import com.ecommerce.ecommApp.orders.repository.OrderRepository;
import com.ecommerce.ecommApp.products.services.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public class OrderServices {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private Environment environment;

    @Autowired
    private Producer producer;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    /**
     * Get all orders for the given customer id
     *
     * @param customerID ID of the cutsomer whose order history is needed
     * @return list of order of the customer
     * @throws NotFoundException If no order exist for that particular customer
     */
    public List<Orders> getAllOrder(long customerID) throws NotFoundException {
        List<Orders> orders = orderRepository.getOrdersByCustomerId(customerID);
        if (orders.size() == 0)
            throw new NotFoundException("No order Found");
        return orders;
    }

    /**
     * Function to add order to the orders database
     *
     * @param customerID      ID of the customer placing the order
     * @throws Exception
     */
    public void placeOrder(Long customerID) throws Exception {

        List<Cart>  cart = fetchCartOfCustomer(customerID);
        List<ItemsDTO> productsOrdered = convertToItemsDto(cart);
        for (ItemsDTO item : productsOrdered) {
            Orders order = getOrderInstance(customerID, item);
            orderRepository.save(order);
            notifyUser(Arrays.asList(NotificationType.Text_SMS.toString(), NotificationType.EMAIL.toString()), order);
        }
        productService.deductProducts(productsOrdered);
    }

    private List<ItemsDTO> convertToItemsDto(List<Cart> cartProducts){

        List<ItemsDTO> items = new ArrayList<>();
        for(Cart cartProduct: cartProducts){

            ItemsDTO cartItem = new ItemsDTO();
            cartItem.setCost(cartProduct.getCost());
            cartItem.setProductID(cartProduct.getCartIdentity().getProductId());
            cartItem.setQuantity(cartProduct.getQuantity());
            items.add(cartItem);
        }
        return items;
    }

    /**
     * Helper function to convert Item type object into Orders Type
     *
     * @param customerId
     * @param item
     * @return An orders instance
     */
    private Orders getOrderInstance(Long customerId, ItemsDTO item) {
        Orders orders = new Orders();
        orders.setCustomerID(customerId);
        orders.setProductID(item.getProductID());
        orders.setTotalCost(item.getCost());
        orders.setQuantity(item.getQuantity());
        orders.setOrderStatus(OrderStatus.Placed.toString());
        return orders;
    }

    private List<Cart> fetchCartOfCustomer(Long customerId) throws Exception {

        String token = httpServletRequest.getHeader("Authorization");
        String data = Communication.sendGetRequest("http://" + Communication.getApplicationAddress() + "/carts/" + customerId,token);
        ObjectMapper objectMapper = CommonsUtil.getObjectMapper();
        List<Cart> items = objectMapper.readValue(data, new TypeReference<List<Cart>>(){});
        OrderServices.log.info(items.toString());
        return items;
    }

    /**
     * Utility function to add the order to the Kafka notification channel
     *
     * @param modes List of communication modes to notify the customer (Email, Text_SMS, Whatsapp)
     * @param order Instance of the order whose notification is to be raised
     * @throws Exception
     */
    private void notifyUser(List<String> modes, Orders order) throws Exception {
        ObjectMapper objectMapper = CommonsUtil.getObjectMapper();
        String token = httpServletRequest.getHeader("Authorization");
        System.out.println(token);
        CustomerDto customer = objectMapper.readValue
                (Communication.sendGetRequest("http://" + Communication.getApplicationAddress() + "/customer/" + order.getCustomerID(),token)
                        , CustomerDto.class);
        OrderDetails orderDetails = createOrderPlacedInstance(modes, order, customer);
        Properties props = producer.getProducerConfigs();
        KafkaProducer<String, String > kafkaProducer= producer.getKafkaProducer(props);
        producer.producerRecord(objectMapper.writeValueAsString(orderDetails),
                environment.getRequiredProperty(CommonsUtil.NOTIFICATION_ORDER_PLACED_TOPIC),kafkaProducer);
        producer.closeProducer(kafkaProducer);
    }

    /**
     * Hellper function in order to get the relevant details to be passed to the notify user function
     *
     * @param modes    modes of communication medium needed to notify the user
     * @param order    Orders instance containing the detail of ordered item
     * @param customer Customer instance containing the details of customer item
     * @return Instance of orderPlaced containing relevant details
     * @throws Exception
     */
    private OrderDetails createOrderPlacedInstance(List<String> modes, Orders order, CustomerDto customer) throws Exception {
        OrderDetails orderPlaced = new OrderDetails();
        orderPlaced.setMode(modes);
        orderPlaced.setCustomerDto(customer);
        orderPlaced.setOrderID(order.getOrderID());
        Product product = fetchProduct(order.getProductID());
        orderPlaced.setProductName(product.getName());
        orderPlaced.setQuantity(order.getQuantity());
        orderPlaced.setTotalCost(order.getTotalCost());
        return orderPlaced;
    }

    /**
     * Helper function in order to fetch the Product details form the products microservice
     *
     * @param productId ID of the product whose details are needed to be fetched
     * @return Product instance that contains the details of the products
     * @throws Exception
     */
    private Product fetchProduct(Long productId) throws Exception {
        String token = httpServletRequest.getHeader("Authorization");
        String data = Communication.sendGetRequest("http://" + Communication.getApplicationAddress() + "/product/" + productId,token);
        ObjectMapper objectMapper = CommonsUtil.getObjectMapper();
        return objectMapper.readValue(data, Product.class);
    }

    /**
     * Function to return the current order status
     *
     * @param orderID UUID of the order whose status is being queried
     * @return the order status as string
     */
    public String getOrderStatus(String orderID) {
        Orders order = orderRepository.findById(orderID).get();
        return order.getOrderStatus();
    }

    /**
     * Function to return an order based on order id
     *
     * @param orderId UUID of the order
     * @return OrderDTo instance of the order
     * @throws NotFoundException Thrown if UUID is not present in database
     */
    public OrdersDTO getOrderDetails(String orderId) throws NotFoundException {
        OrdersDTO ordersDTO = createOrderDtoInstance(orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order Not found")));
        return ordersDTO;
    }

    /**
     * Helper function to convert order to orderDto
     *
     * @param orders
     * @return
     */
    private OrdersDTO createOrderDtoInstance(Orders orders) {
        OrdersDTO ordersDTO = new OrdersDTO();
        ordersDTO.setOrderID(orders.getOrderID());
        ordersDTO.setCustomerID(orders.getCustomerID());
        ordersDTO.setProductID(orders.getProductID());
        ordersDTO.setQuantity(orders.getQuantity());
        ordersDTO.setCost(orders.getTotalCost());
        ordersDTO.setStatus(orders.getOrderStatus());
        return ordersDTO;
    }

    /**
     * Function to update order status
     *
     * @param orderID     UUID of the order
     * @param updateOrder Updates order details
     * @return Success message
     * @throws NotFoundException thrown if UUID not found in database
     */
    public String updateOrderStatus(String orderID, OrdersDTO updateOrder) throws NotFoundException {
        Orders order = orderRepository.findById(orderID).orElseThrow(() -> new NotFoundException("order not found"));
        String initial = order.getOrderStatus();
        order.setOrderStatus(updateOrder.getStatus());
        orderRepository.save(order);
        return "Order status changed from " + initial + " to " + updateOrder.getStatus();
    }

    public Orders buyNow(ItemsDTO itemsDTO) throws Exception {
        String token = httpServletRequest.getHeader("Authorization");
        long customerId = jwtTokenProvider.getCustomerId();
        ObjectMapper objectMapper = CommonsUtil.getObjectMapper();
        CustomerDto customer = objectMapper.readValue
                (Communication.sendGetRequest("http://" + Communication.getApplicationAddress() + "/customer/" + customerId ,token)
                        , CustomerDto.class);
        Orders order = new Orders();
        order.setCustomerID(customer.getId());
        order.setProductID(itemsDTO.getProductID());
        order.setQuantity(itemsDTO.getQuantity());
        order.setTotalCost(itemsDTO.getCost());
        order.setOrderStatus(OrderStatus.Placed.toString());
        return orderRepository.save(order);
    }
}