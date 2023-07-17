package com.atdxt;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Entity
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "modified_on")
    private String modifiedOn;

    @Column(name = "created_on", updatable = false)
    private String createdOn;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonBackReference
    private User user;

    // Constructors, getters, and setters

    // Default constructor
    public Auth() {
    }

    // Constructor
    public Auth(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

   /* public void encryptPassword() {
        this.password = Base64.getEncoder().encodeToString(this.password.getBytes());
    }*/


    public void decryptPassword() {
        this.password = new String(Base64.getDecoder().decode(this.password));
    }




}

