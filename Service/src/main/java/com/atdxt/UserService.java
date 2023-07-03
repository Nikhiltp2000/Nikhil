package com.atdxt;

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



    public User saveUser(User user) {
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
    }




    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy:MM:dd HH:mm:ss");
        return dateFormat.format(date);
    }
}