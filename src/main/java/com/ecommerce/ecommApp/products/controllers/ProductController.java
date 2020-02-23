package com.ecommerce.ecommApp.products.controllers;

import com.ecommerce.ecommApp.commons.pojo.ResponseMessage;
import com.ecommerce.ecommApp.commons.pojo.orders.ItemsDTO;
import com.ecommerce.ecommApp.commons.pojo.products.Product;
import com.ecommerce.ecommApp.products.exceptions.ElementNotFoundException;
import com.ecommerce.ecommApp.products.exceptions.NotEnoughQuantityException;
import com.ecommerce.ecommApp.products.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @RequestMapping(path = "/display", method = RequestMethod.GET)
    private List<Product> getAllProducts() {
        return productService.getProductsList();
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    private ResponseEntity<Object> addProduct(@RequestBody Product product) {
        try {
            return new ResponseEntity<Object>(productService.createProduct(product), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to add product to the db.."+ e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/update", method = RequestMethod.PUT)
    private ResponseEntity<Object> updateProduct(@RequestBody Product product) {
        try {
            return new ResponseEntity<>(productService.updateProduct(product), HttpStatus.OK);
        } catch (ElementNotFoundException e) {
            return new ResponseEntity("Unable to update the product details" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/increaseProduct", method = RequestMethod.PUT)
    private ResponseEntity<Object> reStockProducts(@RequestBody Product product) {
        try {
            return new ResponseEntity<>(productService.increaseProductCount(product), HttpStatus.OK);
        } catch (ElementNotFoundException e) {
            return new ResponseEntity<>("Unable to increase the quantity of existing product." + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/category", method = RequestMethod.GET)
    private ResponseEntity<Object> displayCategory() {
        try {
            logger.info("fetching categories from the db...");
            return new ResponseEntity<>(productService.getAllCategories(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Unable to select categories" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/deductInventory", method = RequestMethod.PUT)
    private ResponseEntity<Object> deductFromInventory(@RequestBody List<ItemsDTO> products) {
        try {
            logger.info("fetching elements to deduct {}", products);
            productService.deductProducts(products);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (NotEnoughQuantityException | ElementNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/{productId}", method = RequestMethod.GET)
    private ResponseEntity<Object> getByProductId(@PathVariable long productId) {
        try {
            logger.info("Fetching element {} from Products.", productId);
            return new ResponseEntity(productService.getProduct(productId), HttpStatus.OK);
        } catch (ElementNotFoundException e) {
            return new ResponseEntity("ProductId not found" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/generate", method = RequestMethod.GET)
    public ResponseEntity<Object> generateProducts() {
        try {
            productService.generateProducts();
            return new ResponseEntity(new ResponseMessage("Products are Inserted", "GENERATED"), HttpStatus.CREATED);
        } catch (Exception ex) {
            return new ResponseEntity(new ResponseMessage("Products are not Inserted", "ERROR"), HttpStatus.BAD_REQUEST);
        }
    }
}

