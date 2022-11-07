package com.sanath.springBootMongoDB.collection;

import lombok.Builder;
import lombok.Data;

@Data //Getter and setter methods, default constructor..
@Builder //To implement the builder pattern

public class Address {
    private String address1;
    private String address2;
    private String city;
}
