package com.ecommerce.ecommApp.commons.security;

import com.ecommerce.ecommApp.customers.models.Customer;
import com.ecommerce.ecommApp.customers.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private CustomerRepository customerRepository;
    private CustomerPrincipal customerPrincipal;

    @Autowired
    public CustomUserDetailsService(CustomerRepository customerRepository, CustomerPrincipal customerPrincipal) {
        this.customerRepository = customerRepository;
        this.customerPrincipal = customerPrincipal;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        // Let people login with either username or email
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + email)
                );
        return customerPrincipal.create(customer);
    }

    // This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
        return customerPrincipal.create(customer);
    }
}