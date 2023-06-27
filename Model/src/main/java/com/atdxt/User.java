package com.atdxt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
//    @JsonIgnoreProperties("user")
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY )
    @JsonIgnoreProperties("user")
    private Address address;

    private Timestamp modifiedOn;
    @Column(name = "created_on", updatable = false)
    private Timestamp createdOn;
    // Other properties, constructors, getters, and setters

//    @Column(name = "created_on" ,updatable = false)
//    private Timestamp createdOn;


//    @Column(name = "modified_on")
//    private Timestamp modifiedOn;

    //default constructor
    public User() {
    }

    // Constructor
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
        address.setUser(this);
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Timestamp modifiedOn) {
        this.modifiedOn = modifiedOn;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @PrePersist
    public void prePersist(){
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        createdOn = currentTimestamp;
        modifiedOn = currentTimestamp;
    }

    @PreUpdate
    public void preUpdate() {
        modifiedOn = Timestamp.from(Instant.now());
    }
}



