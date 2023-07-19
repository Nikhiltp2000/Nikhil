package com.atdxt;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final AuthRepository authRepository;


    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.authRepository= authRepository;
    }
    //Get all users
    public List<User> getAllUsers() {
        logger.info("Fetching all user data");
        return userRepository.findAll();
    }

    //Search user by id
    public Optional<User> getUserById(Long id){
        logger.info("Fetching user by ID: {}" , id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setModifiedOn(formatDate(new Date()));
            userRepository.save(user);
        }
        return optionalUser;
    }


// original code
/*    public User saveUser(User user) {
        logger.info("Saving user: {}", user.getName());
        user.setCreatedOn(formatDate(new Date()));
        user.setModifiedOn(formatDate(new Date()));

        if (user.getAddress() != null) {
            Address address = user.getAddress();
            address.setUser(user);
            address.setCreatedOn(formatDate(new Date()));
            address.setModifiedOn(formatDate(new Date()));
            address.setCity(address.getCity());
            address.setCountry(address.getCountry());
            address.setStreet(address.getStreet());
           // addressRepository.save(address);
            user.setAddress(address);
        }



        return userRepository.save(user);
    }*/
//Save user
    public User saveUser(User user, boolean isUpdate) {
        logger.info("Saving user: {}", user.getName());
        user.setCreatedOn(formatDate(new Date()));
        user.setModifiedOn(formatDate(new Date()));

        if (!isUpdate && isEmailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (!isUpdate && isNameExists(user.getName())) {
            throw new IllegalArgumentException("Name already exists");
        }

        if (user.getAddress() != null && user.getAddress().getStreet() != null &&
                user.getAddress().getCity() != null && user.getAddress().getCountry() != null) {
            // Save user with new columns
            return saveUserWithNewColumns(user);
        } else {
            // Save user without new columns
            return saveUserWithoutNewColumns(user);
        }
    }

    private User saveUserWithNewColumns(User user) {
        if (user.getAddress() != null) {
            Address address = user.getAddress();
            address.setUser(user);
            address.setCreatedOn(formatDate(new Date()));
            address.setModifiedOn(formatDate(new Date()));
            address.setCity(address.getCity());
            address.setCountry(address.getCountry());
            address.setStreet(address.getStreet());
            // addressRepository.save(address);
            user.setAddress(address);
        }
        return userRepository.save(user);
    }
    private User saveUserWithoutNewColumns(User user) {
        if (user.getAddress() != null) {
            Address address = user.getAddress();
            address.setUser(user);
            address.setCreatedOn(formatDate(new Date()));
            address.setModifiedOn(formatDate(new Date()));
            address.setCity(address.getCity());
            address.setCountry(address.getCountry());
            address.setStreet(address.getStreet());
            // addressRepository.save(address);
            user.setAddress(address);
        }
        user.setLastname(null);
        user.setRole(null);
        return userRepository.save(user);
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy:MM:dd HH:mm:ss");
        return dateFormat.format(date);
    }

    //Email validation
    public boolean isValidEmail(String email){
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && Pattern.matches(emailRegex, email);
    }

    public boolean isNameExists(String name) {
        return userRepository.existsByName(name);
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }


    public Auth getUserByUsername(String username) {
        //System.out.println("Getting user details for username: " + username);

        System.out.println("User details retrieved: " + authRepository.findByUsername(username)
                .orElse(null));
        return authRepository.findByUsername(username)
                .orElse(null);
    }


}