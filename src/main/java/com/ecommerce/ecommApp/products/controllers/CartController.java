package com.ecommerce.ecommApp.products.controllers;

import com.ecommerce.ecommApp.commons.exceptions.CustomerNotFoundException;
import com.ecommerce.ecommApp.commons.pojo.ResponseMessage;
import com.ecommerce.ecommApp.commons.pojo.products.Cart;
import com.ecommerce.ecommApp.orders.Models.Orders;
import com.ecommerce.ecommApp.orders.services.OrderServices;
import com.ecommerce.ecommApp.products.composite.CartIdentity;
import com.ecommerce.ecommApp.products.payload.CartItem;
import com.ecommerce.ecommApp.products.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author Krishna
 * This RestController will handle all the endpoints requests starting with /carts.
 */
@RestController
public class CartController {


    @Autowired
    private CartService cartService;

    /**
     * This is an Rest Api endpoint mapping which will handle the adding of item to the cart for a particular customer.
     *
     * @return : Object of ResponseMessage class with a proper HTTP request status
     */
    @RequestMapping(value = "/carts", method = RequestMethod.POST)
    private ResponseEntity<Object> addToCart(@RequestBody CartItem cartItem) {
        try {
            Cart cart = cartService.addToCart(cartItem);
            return new ResponseEntity<>(new ResponseMessage("Item successfully added to the cart", "CREATED"), HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResponseMessage("Item was not added in cart due to following error : " + ex.getMessage(), "FAILED")
                    , HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * This is an Rest Api endpoint mapping which will handle the deleting of item from the cart for a particular customer Id.
     *
     * @return : Object of ResponseMessage class with a proper HTTP request status
     */
    @RequestMapping(value = "/carts", method = RequestMethod.DELETE)
    private ResponseEntity<Object> deleteFromCart(@RequestParam Long customer_id, @RequestParam Long product_id) {
        try {
            CartIdentity cartIdentity = new CartIdentity(customer_id, product_id);
            Cart cart = cartService.deleteFromCart(cartIdentity);
            return new ResponseEntity<>(new ResponseMessage("Item successfully deleted from the cart for customer : " + cartIdentity.getCustomerId(), "DELETED")
                    , HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResponseMessage("Item was not deleted from cart due to following error : " + ex.getMessage(), "FAILED")
                    , HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * This is an Rest Api endpoint mapping which will return a list of Items present in the cart for a specific customer.
     * It expects a customer id as a path variable in the url.
     */

    @RequestMapping(value = "/carts/{customerId}", method = RequestMethod.GET)
    private ResponseEntity<Object> getCustomerCart(@PathVariable Long customerId) {
        try {
            List<Cart> fetchedCart = cartService.getCart(customerId);
            return new ResponseEntity<>(fetchedCart, HttpStatus.OK);
        } catch (CustomerNotFoundException ex) {
            return new ResponseEntity<>(new ResponseMessage("Customer Not present for Customer Id : " + customerId, "ERROR"), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResponseMessage("Error in Retrieving cart : " + ex.getMessage(), "ERROR"), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * This is an Rest Api endpoint mapping which will handle the updating of a item already present in the cart for a particular customer.
     *
     * @return : Object of ResponseMessage class with a proper HTTP request status
     */
    @RequestMapping(value = "/carts", method = RequestMethod.PUT)
    private ResponseEntity<Object> updateCustomerCart(@RequestBody CartItem updateCart) {
        try {
            Cart cart = cartService.updateCart(updateCart);
            return new ResponseEntity<>(new ResponseMessage("Cart Item Successfully Updated for customerId " + updateCart.getCustomerId()
                    , "UPDATED"), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResponseMessage("Cart Item is Not Updated for customerId " + updateCart.getCustomerId()
                    + " due to : " + ex.getMessage()
                    , "ERROR"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value="/checkoutCart",method = RequestMethod.POST)
    private ResponseEntity<Object> checkoutCart(@RequestParam Long productId,@RequestParam Long customerId) {
        try {
            Orders order = cartService.checkoutCart(productId, customerId);
            return new ResponseEntity<>(new ResponseMessage("cartItem successfully checked out and Order successfully placed" + order,
                    "CHECKEDOUT"), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResponseMessage("order not placed for customerId" + customerId
                    +" due to : " + ex.getMessage()
                    , "ERROR"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

}



