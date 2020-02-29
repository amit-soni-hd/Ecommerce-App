package com.ecommerce.ecommApp.customers.utils;

import com.ecommerce.ecommApp.commons.pojo.ResponseMessage;
import com.ecommerce.ecommApp.commons.pojo.customer.CustomerDto;
import com.ecommerce.ecommApp.customers.models.Customer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerUtil {

    public CustomerDto convertToPojo(Customer customer) {
        CustomerDto customerDto=new CustomerDto();
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setGender(customer.getGender());
        customerDto.setId(customer.getCustomerId());
        customerDto.setNumber(customer.getNumber());
        customerDto.setWhatsapp(customer.getWhatsapp());
        return customerDto;
    }
    public boolean isStringFieldValid(String value){
        value = value.trim();
        if(value!=null && !value.isEmpty() && value.length()>1){
            return true;
        }
        throw new IllegalArgumentException(String.valueOf(new ResponseMessage("Value is wrong ","error")));
    }

    public boolean isPhoneNumberValid(String phoneNumber){
        Pattern p = Pattern.compile("[7-9][0-9]{9}");
        Matcher matcher = p.matcher(phoneNumber);
        return (matcher.find() && matcher.group().equals(phoneNumber));
    }
}
