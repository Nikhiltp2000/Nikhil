package com.atdxt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;
import java.util.Date;
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all user data");
        return userRepository.findAll();
      //  return userRepository.findAllFetchAddress();
    }

    //Search user by id
    public Optional<User> getUserById(Long id){
        logger.info("Fetching user by ID: {}" , id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
          //  Timestamp createdOn = user.getCreatedOn();
          //  user.setModifiedOn(Timestamp.from(Instant.now()));
            user.setModifiedOn(formatDate(new Date()));
          //  user.setCreatedOn(user.getCreatedOn());
            userRepository.save(user);
        }

        return optionalUser;
    }

//    public User saveUser(User user) {
//        logger.info("Saving user: {}", user.getName());
//        Timestamp currentTimestamp = Timestamp.from(Instant.now());
//        user.setCreatedOn(currentTimestamp);
//        user.setCreatedOn(currentTimestamp);
//
//        //userRepository.save(user);
//
//        if (user.getAddress() != null) {
//            Address address = user.getAddress();
//            address.setUser(user);
//            addressRepository.save(address);
//        }
//
//
//
//        return userRepository.save(user);
//    }

    public User saveUser(User user) {
        logger.info("Saving user: {}", user.getName());
        user.setCreatedOn(formatDate(new Date()));
        user.setModifiedOn(formatDate(new Date()));

     //   Timestamp currentTimestamp = Timestamp.from(Instant.now());
       // user.setCreatedOn(currentTimestamp);
        //user.setModifiedOn(currentTimestamp);


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