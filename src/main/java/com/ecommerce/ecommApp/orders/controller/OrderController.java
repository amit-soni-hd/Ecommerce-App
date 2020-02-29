package com.ecommerce.ecommApp.orders.controller;

import com.ecommerce.ecommApp.commons.pojo.ResponseMessage;
import com.ecommerce.ecommApp.commons.pojo.orders.ItemsDTO;
import com.ecommerce.ecommApp.commons.pojo.orders.OrdersDTO;
import com.ecommerce.ecommApp.orders.Models.Orders;
import com.ecommerce.ecommApp.orders.services.OrderServices;
import javassist.NotFoundException;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderServices orderService;

    /**
     * Get Order history for a customer
     * @param customerID
     * @return List of Orders
     */
    @GetMapping("/{customerID}")
    private ResponseEntity<Object> getOrderByCustomerId(@PathVariable long customerID) {
        try {
            return new ResponseEntity<>(orderService.getAllOrder(customerID), HttpStatus.OK);
        } catch (NotFoundException err) {
            return new ResponseEntity<>(new ResponseMessage("No order forund for customer id:" + customerID, "Error"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Stores and places orders when a customer hit checkout.
     * @param customerID
     * @return Success message
     * @throws Exception
     */
    @RequestMapping(path = "/{customerID}", method = RequestMethod.POST)
    private ResponseEntity<ResponseMessage> placeOrder(@PathVariable long customerID) throws Exception {
        try {
            orderService.placeOrder(customerID);
            return new ResponseEntity<>(new ResponseMessage("Success order placed", "created"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Error in placing order", "Error"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get details of a particular order
     * @param orderID UUID of the order
     * @return Order
     */
    @GetMapping("/status/{orderID}")
    private ResponseEntity<Object> getOrderDetail(@PathVariable String orderID) {
        try {
            OrdersDTO ordersDTO = orderService.getOrderDetails(orderID);
            return new ResponseEntity<>(ordersDTO, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ResponseMessage("Element with " + orderID + "not found. Ensure the correct order id is provided", "Error"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update a particular order
     * @param updateOrder Updated value of the Order
     * @param orderID     UUID of the order
     * @return Updates message or notify user of an error
     */
    @PutMapping("/update/{orderID}")
    private ResponseEntity<ResponseMessage> updateStatus(@RequestBody OrdersDTO updateOrder, @PathVariable String orderID) {
        try {
            String message = orderService.updateOrderStatus(orderID, updateOrder);
            return new ResponseEntity<>(new ResponseMessage(message, "Updated"), HttpStatus.OK);
        } catch (NotFoundException err) {
            return new ResponseEntity<>(new ResponseMessage("Order not found", "Error"), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("The following error occurred " + e.getMessage(), "Error"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/buynow")
    private ResponseEntity<ResponseMessage> orderNow(@RequestBody ItemsDTO itemsDTO) {
        try {
            Orders orders = orderService.buyNow(itemsDTO);
            return new ResponseEntity<>(new ResponseMessage(orders.toString(), "New order placed"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Order cannot placed" + e.getMessage(), "Error"), HttpStatus.BAD_REQUEST);
        }
    }

}
