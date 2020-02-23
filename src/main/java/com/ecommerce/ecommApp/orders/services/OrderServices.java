package com.ecommerce.ecommApp.orders.services;

import com.ecommerce.ecommApp.EcommAppApplication;
import com.ecommerce.ecommApp.commons.NotificationProducer;
import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.Util.Communication;
import com.ecommerce.ecommApp.commons.enums.NotificationType;
import com.ecommerce.ecommApp.commons.enums.OrderStatus;
import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.commons.pojo.notification.OrderPlaced;
import com.ecommerce.ecommApp.commons.pojo.orders.ItemsDTO;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.orders.Models.Orders;
import com.ecommerce.ecommApp.orders.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServices {
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Get all orders for the given customer id
     *
     * @param customerID
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
     * @param productsOrdered Th elist of Items that are ordered by the customer
     * @throws Exception
     */
    public void placeOrder(Long customerID, List<ItemsDTO> productsOrdered) throws Exception {
        for (ItemsDTO item : productsOrdered) {
            Orders order = getOrderInstance(customerID, item);
            orderRepository.save(order);
            notifyUser(Arrays.asList(NotificationType.Text_SMS.toString(), NotificationType.EMAIL.toString()), order);
        }
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

    /**
     * Utility function to add the order to the Kafka notification channel
     *
     * @param modes List of communication modes to notify the customer (Email, Text_SMS, Whatsapp)
     * @param order Instance of the order whose notification is to be raised
     * @throws Exception
     */
    private void notifyUser(List<String> modes, Orders order) throws Exception {
        ObjectMapper objectMapper = CommonsUtil.getObjectMapper();
        CustomerDto customer = objectMapper.readValue
                (Communication.sendGetRequest("http://" + Communication.getApplicationAddress() + "/customer/" + order.getCustomerID())
                        , CustomerDto.class);
        OrderPlaced orderPlaced = createOrderPlacedInstance(modes, order, customer);
        NotificationProducer notificationProducer = CommonsUtil.getNotificationProducer();
        notificationProducer.producerNotification(objectMapper.writeValueAsString(orderPlaced),
                EcommAppApplication.environment.getRequiredProperty("notification.order.placed.topic"));
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
    private OrderPlaced createOrderPlacedInstance(List<String> modes, Orders order, CustomerDto customer) throws Exception {
        OrderPlaced orderPlaced = new OrderPlaced();
        orderPlaced.setMode(modes);
        orderPlaced.setCustomerDto(customer);
        orderPlaced.setOrderID(order.getOrderID());
        Product product = fetchProduct(order.getProductID());
        orderPlaced.setProductName(product.getName());
        orderPlaced.setQuandity(order.getQuantity());
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
        String data = Communication.sendGetRequest("http://" + Communication.getApplicationAddress() + "/product/" + productId);
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

}