package com.atdxt.JpaConnection.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.atdxt.JpaConnection.Model.User;
import com.atdxt.JpaConnection.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        logger.info("Fetching all user data");
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        logger.info("Saving user: {}", user.getName());
        return userRepository.save(user);
    }
}