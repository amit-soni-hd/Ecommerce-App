package com.ecommerce.ecommApp.customers.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "customer_address")
@Data
public class CustomerAddress {

    @JsonIgnore
    @Id
    @Column(name = "address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(name="house_number")
    private String houseNumber;

    @Column(name="street_name")
    private String street;

    @Column(name="city")
    private String city;

    @Column(name="state")
    private String state;

    @Column(name="pincode")
    private String pincode;

    @Column(name="type")
    private String type;

    @Column(name="landmark")
    private String landmark;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
