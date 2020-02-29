package com.ecommerce.ecommApp.customers.services;

import com.ecommerce.ecommApp.commons.Util.CommonsUtil;
import com.ecommerce.ecommApp.commons.enums.NotificationType;
import com.ecommerce.ecommApp.commons.kafka.Producer;
import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.commons.pojo.notification.UserRegistered;
import com.ecommerce.ecommApp.customers.dto.RegistrationDto;
import com.ecommerce.ecommApp.customers.exceptions.EmailExistsException;
import com.ecommerce.ecommApp.customers.models.Customer;
import com.ecommerce.ecommApp.customers.models.CustomerAddress;
import com.ecommerce.ecommApp.customers.repository.CustomerAddressRepository;
import com.ecommerce.ecommApp.customers.repository.CustomerRepository;
import com.ecommerce.ecommApp.customers.utils.CustomerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;
    private Producer producer;
    private CustomerUtil customerUtil;
    private PasswordEncoder passwordEncoder;
    private CustomerAddressRepository customerAddressRepository;
    private  Environment environment;

    @Autowired
    private CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
                            Producer producer, Environment environment, CustomerAddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.producer = producer;
        this.customerUtil = new CustomerUtil();
        this.passwordEncoder = passwordEncoder;
        this.customerAddressRepository = addressRepository;
        this.environment=environment;
    }


    public CustomerDto register(RegistrationDto regDetails) throws EmailExistsException {

        if (emailExists(regDetails.getEmail())) {
            throw new EmailExistsException(
                    CommonsUtil.EMAIL_EXISTS + regDetails.getEmail());
        }
        Customer customer = new Customer();
        customer.setName(regDetails.getName());
        customer.setEmail(regDetails.getEmail());
        customer.setNumber(regDetails.getNumber());
        customer.setPassword(passwordEncoder.encode(regDetails.getPassword()));
        customer.setWhatsapp(regDetails.getWhatsapp());
        customer.setGender(regDetails.getGender());
        customerRepository.save(customer);
        CustomerDto customerDto = customerUtil.convertToPojo(customer);
        sendRegistrationNotification(customerDto);
        return customerDto;
    }

    public CustomerDto getCustomerDetails(Long customerId) throws NotFoundException {
        Optional<Customer> loggedInCustomer = customerRepository.findById(customerId);
        if (loggedInCustomer.isPresent()) {
            return customerUtil.convertToPojo(loggedInCustomer.get());
        } else {
            throw new NotFoundException(CommonsUtil.WRONG_CUSTOMER_ID);
        }
    }

    public CustomerDto updateCustomerDetails(CustomerDto customerDetails) {
        Optional<Customer> registeredCustomer = customerRepository.findById(customerDetails.getId());
        if (registeredCustomer.isPresent()) {

            Customer customer = registeredCustomer.get();
            if (customerUtil.isStringFieldValid(customerDetails.getEmail()))
                customer.setEmail(customerDetails.getEmail());
            if (customerUtil.isStringFieldValid(customerDetails.getName()))
                customer.setName(customerDetails.getName());
            if (customerUtil.isStringFieldValid(customerDetails.getGender()))
                customer.setGender(customerDetails.getGender());
            if (customerUtil.isPhoneNumberValid(customerDetails.getNumber().toString()))
                customer.setNumber(customerDetails.getNumber());
            if (customerUtil.isPhoneNumberValid(customerDetails.getWhatsapp().toString()))
                customer.setWhatsapp(customerDetails.getWhatsapp());

            customerRepository.save(customer);
            return customerUtil.convertToPojo(customer);
        } else {
            throw new NoSuchElementException(CommonsUtil.CUSTOMER_NOT_FOUND);
        }
    }

    public CustomerAddress addCustomerAddress(CustomerAddress addressDetails, Long customerId){

        if(customerRepository.findById(customerId).isPresent()) {

            Customer customer = customerRepository.findById(customerId).get();
            addressDetails.setCustomer(customer);
            customerAddressRepository.save(addressDetails);
            return addressDetails;
        }
        else
            throw new NoSuchElementException(CommonsUtil.CUSTOMER_NOT_FOUND);
    }

    public List<CustomerAddress> getCustomerAddresses(Long customerId){

        if(customerRepository.findById(customerId).isPresent()){
            Customer customer = customerRepository.findById(customerId).get();
            return customerAddressRepository.findByCustomer(customer);
        } else
            throw new NoSuchElementException(CommonsUtil.CUSTOMER_NOT_FOUND);
    }
    private void sendRegistrationNotification(CustomerDto customerDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserRegistered userRegistered = new UserRegistered();
        userRegistered.setMode(new ArrayList<>());
        userRegistered.getMode().add(NotificationType.Text_SMS.toString());
        userRegistered.getMode().add(NotificationType.EMAIL.toString());
        userRegistered.setCustomerDto(customerDto);
        try {
            Properties props = producer.getProducerConfigs();
            KafkaProducer<String, String > kafkaProducer= producer.getKafkaProducer(props);
            producer.producerRecord(objectMapper.writeValueAsString(userRegistered),
                    environment.getRequiredProperty(CommonsUtil.NOTIFICATION_REGISTERED_TOPIC),kafkaProducer);
            producer.closeProducer(kafkaProducer);
        } catch (IOException ignored) {
        }
    }

    private boolean emailExists(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        return customer.isPresent();
    }

}
