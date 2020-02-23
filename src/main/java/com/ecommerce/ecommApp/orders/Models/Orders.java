package com.ecommerce.ecommApp.orders.Models;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Data
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id")
    private String orderID;

    @Column(name = "customer_id")
    private Long customerID;

    @Column(name = "product_id")
    private Long productID;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_cost")
    private Float totalCost;

    @Column(name = "order_status")
    private String orderStatus;

}
/*
    @GetMapping("/{customerID}")
    http://localhost:3000/orders/1



    @RequestMapping(path = "/{customerID}", method = RequestMethod.POST)
    http://localhost:3000/orders/1
    [
        {
             "product_id":1,
             "quantity":2,
             "total_cost":1
        },
        {
             "product_id":1,
             "quantity":2,
             "total_cost":1
        },
        {
             "product_id":1,
             "quantity":2,
             "total_cost":1
        }
    ]


    @GetMapping("/status/{orderID}")
    http://localhost:3000/orders/status/1a842a74-f6db-493f-907f-44e93df40372


    @PutMapping("/update/{orderID}")
    http://localhost:3000/orders/update/1a842a74-f6db-493f-907f-44e93df40372
    {
	"order_id": "1a842a74-f6db-493f-907f-44e93df40372",
    "customer_id": 1,
    "product_id": 1,
    "quandity": 1,
    "total_cost": 1,
    "status": "Delivered"
}*/
