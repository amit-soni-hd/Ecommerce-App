package com.ecommerce.ecommApp.products.services;

import com.ecommerce.ecommApp.commons.exceptions.CustomerNotFoundException;
import com.ecommerce.ecommApp.commons.pojo.products.Cart;
import com.ecommerce.ecommApp.products.composite.CartIdentity;
import com.ecommerce.ecommApp.products.payload.CartItem;
import com.ecommerce.ecommApp.products.repositories.CartRepository;
import com.ecommerce.ecommApp.products.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Author Krishna
 * This class will work as a service class for the CartController.
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * This function adds requested productIds  to the cart of the customer
     * @param  payload CartItem as argument
     * @return :List of Objects of Cart which are added to the cart of customer
     */

    public Cart addToCart(CartItem payload) {
        Cart cart = new Cart();
        CartIdentity cartIdentity = new CartIdentity(payload.getCustomerId(), payload.getProductId());
        cart.setCartIdentity(cartIdentity);
        cart.setQuantity(payload.getQuantity());
        cart.setCost(payload.getCost());
        cartRepository.save(cart);
        return cart;
    }

    /**
     * This function deletes requested products from the Cart
     * Takes composite id CartIdentity as argument
     * @param  identity
     * @return :Object of Cart which is deleted
     */
    public Cart deleteFromCart(CartIdentity identity) {
        Cart cart = cartRepository.findById(identity).get();
        cartRepository.delete(cart);
        return cart;
    }

    /**
     * This function fetches list of Cart for a particular customer
     * Takes customerId as argument
     * @param customerId
     * @return
     */

    public List<Cart> getCart(Long customerId) throws CustomerNotFoundException {
        try {
            Optional<List<Cart>> fetchCartItems = cartRepository.findByCustomerId(customerId);
            List<Cart> fetchedCartItems = fetchCartItems.get();
            return fetchedCartItems;
        }
        catch (Exception ex)
        {
            throw new CustomerNotFoundException();
        }
    }

    /**
     * This function updates particular cart item of a particular customer
     * Takes cartItem payload as argument
     * @param payload
     * @return :Updated cart of the customer
     */

    public Cart updateCart(CartItem payload) {
        Cart cart=cartRepository.findById(new CartIdentity(payload.getCustomerId(),payload.getProductId())).get();
        cart.setCost(payload.getCost());
        cart.setQuantity(payload.getQuantity());
        return cartRepository.save(cart);
    }

}
