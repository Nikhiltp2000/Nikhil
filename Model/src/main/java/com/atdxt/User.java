package com.atdxt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

//import java.sql.Timestamp;
//import java.time.Instant;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "info")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

    private String lastname;

    private String role;

    @Column(name = "img_url")
    private String img_url;


//    @JsonIgnoreProperties("user")
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY )
    @JsonIgnoreProperties("user")
    private Address address;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    private Auth auth;

    private String modifiedOn;
    @Column(name = "created_on", updatable = false)
    private String createdOn;
    // Other properties, constructors, getters, and setters

//    @Column(name = "created_on" ,updatable = false)
//    private Timestamp createdOn;


//    @Column(name = "modified_on")
//    private Timestamp modifiedOn;

    //default constructor
    public User() {
    }

    // Constructor
    public User(Long id, String name, String email,String lastname,String role,String img_url) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.lastname=lastname;
        this.role=role;
        this.img_url=img_url;
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    /*public void setImgUrl(String img_url){
        this.img_url=img_url;
    }*/

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    /*public String getImgUrl(){
        return img_url;
    }*/

    public String getImg_url() {
        return img_url;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
        address.setUser(this);
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }



    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(String modifiedOn) {
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

//    @PrePersist
//    public void prePersist(){
//        Timestamp currentTimestamp = Timestamp.from(Instant.now());
//        createdOn = currentTimestamp;
//        modifiedOn = currentTimestamp;
//    }
//
//    @PreUpdate
//    public void preUpdate() {
//        modifiedOn = Timestamp.from(Instant.now());
//    }

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



