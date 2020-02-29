package com.ecommerce.ecommApp.customers.controllers;

import com.ecommerce.ecommApp.commons.pojo.ResponseMessage;
import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.customers.dto.RegistrationDto;
import com.ecommerce.ecommApp.customers.exceptions.EmailExistsException;
import com.ecommerce.ecommApp.customers.models.CustomerAddress;
import com.ecommerce.ecommApp.customers.services.CustomerService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Customer Controller to manage requests for customer module.
 */
@RestController
class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Post method to register a customer
     * @param registrationDetails - Data entered by customer to register
     * @return customerDetails - registered customer's details
     * EmailExistsException - when already used email used for registration.
     */
    @PostMapping("/register")
    private ResponseEntity<Object> registerUser(@RequestBody @Valid RegistrationDto registrationDetails){

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
     * get request for obtaining customer details by its id.
     * @param customerId - registered customer's id.
     * @return - {@link ResponseEntity}
     */
    @GetMapping("/customer/{customerId}")
    private ResponseEntity<Object> getCustomerDetails(@PathVariable long customerId){
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
    private ResponseEntity<Object> updateCustomerDetails(@RequestBody @Valid CustomerDto customerDetails){
        try{
            CustomerDto updatedCustomerDetails = customerService.updateCustomerDetails(customerDetails);
            return new ResponseEntity<>(updatedCustomerDetails,HttpStatus.OK);
        } catch(NoSuchElementException exception){
            return new ResponseEntity<>(new ResponseMessage("Update Unsuccessful. "+exception.getMessage(),"error"),
                    HttpStatus.FORBIDDEN);
        }
    }

    /**
     *
     * @param address -{@link CustomerAddress} customer address details
     * @return - Response entity of saved customer address
     */
    @PostMapping("/customer/address/{id}")
    private ResponseEntity<Object> addAddress(@RequestBody @Valid CustomerAddress address,
                                              @PathVariable(name = "id") Long customerId){
        try{
                CustomerAddress newAddress = customerService.addCustomerAddress(address,customerId);
                return new ResponseEntity<>(newAddress, HttpStatus.OK);

        } catch(NoSuchElementException exception){
            return new ResponseEntity<>(new ResponseMessage("Unsuccessful. "+exception.getMessage(),"error"),
                    HttpStatus.FORBIDDEN);
        }
    }

    /**
     *
     * @return - List<{@link CustomerAddress} in the form of Response Entity.
     */
    @GetMapping("/customer/address/{id}")
    private ResponseEntity<Object> getAddresses(@PathVariable(name = "id") Long customerId){
        try{

                List<CustomerAddress> customerAddressList = customerService.getCustomerAddresses(customerId);
                return new ResponseEntity<>(customerAddressList, HttpStatus.OK);

        }catch(Exception exception){
            return new ResponseEntity<>(new ResponseMessage("Unsuccessful. "+exception.getMessage(),"error"),
                    HttpStatus.FORBIDDEN);
        }
    }
}
