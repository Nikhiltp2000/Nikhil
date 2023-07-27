package com.atdxt;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String street;
    private String city;
    private String country;

    private String modifiedOn;
    @Column(name = "created_on", updatable = false)
    private String createdOn;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    // Constructors, getters, and setters

    // Default constructor
    public Address() {
    }

    // Constructor
    public Address(Long id, String street, String city, String country, User user) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.country = country;
        this.user = user;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // PrePersist and PreUpdate methods
    @PrePersist
    public void prePersist() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        createdOn = formatter.format(currentDate);
        modifiedOn = formatter.format(currentDate);
    }

    @PreUpdate
    public void preUpdate() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        modifiedOn = formatter.format(currentDate);
    }

}

