package com.atdxt;

import com.atdxt.User;
import com.atdxt.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
          //  Timestamp createdOn = user.getCreatedOn();
            user.setModifiedOn(Timestamp.from(Instant.now()));
          //  user.setCreatedOn(user.getCreatedOn());
            userRepository.save(user);
        }

        return optionalUser;
    }

    public User saveUser(User user) {
        logger.info("Saving user: {}", user.getName());
        Timestamp currentTimestamp = Timestamp.from(Instant.now());
        user.setCreatedOn(currentTimestamp);
        user.setCreatedOn(currentTimestamp);
        return userRepository.save(user);
    }
}