package com.ecommerce.ecommApp.customers.customAnnotations;

import com.ecommerce.ecommApp.customers.dto.RegistrationDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * PasswordMatchesValidator class used by @PasswordMatches custom annotation.
 */
public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context){
        RegistrationDto customerDetails = (RegistrationDto)object;
        return customerDetails.getPassword().equals(customerDetails.getMatchingPassword());
    }
}
