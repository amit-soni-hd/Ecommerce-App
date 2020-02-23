package com.ecommerce.ecommApp.customers.controllers;

import com.ecommerce.ecommApp.commons.pojo.ResponseMessage;
import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.customers.dto.LoginDto;
import com.ecommerce.ecommApp.customers.dto.RegistrationDto;
import com.ecommerce.ecommApp.customers.exceptions.EmailExistsException;
import com.ecommerce.ecommApp.customers.services.CustomerService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.NoSuchElementException;

/**
 * Customer Controller to manage requests for customer module.
 */
@RestController
public class CustomerController {

    @Autowired
    CustomerService customerService;

    /**
     * Post method to register a customer
     * @param registrationDetails - Data entered by customer to register
     * @return customerDetails - registered customer's details
     * EmailExistsException - when already used email used for registration.
     */
    @PostMapping("/register")
    ResponseEntity<Object> registerUser(@RequestBody @Valid RegistrationDto registrationDetails){

        try {
            CustomerDto customerDetails =  customerService.register(registrationDetails);
            return new ResponseEntity<>(customerDetails, HttpStatus.OK);
        }
        catch (EmailExistsException exception){
            return new ResponseEntity<>(new ResponseMessage("Registration unsuccessful. ","error"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Post request to login a customer
     * @param loginDetails - login credentials of customer
     * @return - {@link ResponseEntity}
     */
    @PostMapping("/login")
    ResponseEntity<Object> login(@RequestBody @Valid LoginDto loginDetails){

        try{
            CustomerDto customerDetails = customerService.loginCustomer(loginDetails);
            return new ResponseEntity<>(customerDetails,HttpStatus.OK);
        }
        catch (NotFoundException exception){
            return new ResponseEntity<>(new ResponseMessage("Login Unsuccessful. ","error"),
                    HttpStatus.FORBIDDEN);
        }
    }

    /**
     * get request for obtaining customer details by its id.
     * @param customerId - registered customer's id.
     * @return - {@link ResponseEntity}
     */
    @GetMapping("/customer/{customerId}")
    ResponseEntity<Object> getCustomerDetails(@PathVariable long customerId){
        try{
            CustomerDto customerDetails = customerService.getCustomerDetails(customerId);
            return new ResponseEntity<>(customerDetails,HttpStatus.OK);

        } catch (NotFoundException exception) {
            return new ResponseEntity<>(new ResponseMessage("User Id not found. ","error"),
                    HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Put request to update a customer's details
     * @param customerDetails - details to be updated
     * @return - {@link ResponseEntity}
     */
    @PutMapping("/customer")
    ResponseEntity<Object> updateCustomerDetails(@RequestBody @Valid CustomerDto customerDetails){
        try{
            CustomerDto updatedCustomerDetails = customerService.updateCustomerDetails(customerDetails);
            return new ResponseEntity<>(updatedCustomerDetails,HttpStatus.OK);
        } catch(NoSuchElementException exception){
            return new ResponseEntity<>(new ResponseMessage("Update Unsuccessful. "+exception.getMessage(),"error"),
                    HttpStatus.FORBIDDEN);
        }
    }
}
