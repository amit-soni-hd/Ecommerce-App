package com.ecommerce.ecommApp.customers.models;

import lombok.Data;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "customer")
@Data
public class Customer
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false,unique = true)
    private Long customerId;

    @Column(name = "name")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="number")
    private Long number;

    @Column(name="whatsapp")
    private Long whatsapp;

    @Column(name="gender")
    private String gender;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL,targetEntity = CustomerAddress.class)
    private Set<CustomerAddress> customerAddresses;

}
