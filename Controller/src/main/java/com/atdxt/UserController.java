package com.atdxt;



import com.atdxt.User;
import com.atdxt.UserService;

//import org.apache.logging.log4j.LogManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    private final AuthRepository authRepository;

    @Autowired
    public UserController(UserService userService, AuthRepository authRepository) {
        this.userService = userService;
        this.authRepository = authRepository;
    }

    // Fetch all data from the database

    // Fetch all data from the database, Return values in tabular format
    @GetMapping("/getdata")
    public ModelAndView getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            logger.info("Returned {} users", users.size());

            ModelAndView modelAndView = new ModelAndView("users");
            modelAndView.addObject("users", users);
            return modelAndView;
        } catch (Exception e) {
            logger.error("Error occurred while fetching users: {}", e.getMessage());
            ModelAndView errorModelAndView = new ModelAndView("error");
            errorModelAndView.addObject("errorMessage", "Error occurred while fetching users");
            return errorModelAndView;
        }
    }

    @GetMapping("/")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView("home");
        return modelAndView;
    }


    //get mapping to find user by id

//get mapping to find user by id, Return values in tabular format
    @GetMapping("/users/{id}")
    public ModelAndView getUserById(@PathVariable Long id) {
        try {
            Optional<User> optionalUser = userService.getUserById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                logger.info("Found user with ID: {}", id);

                ModelAndView modelAndView = new ModelAndView("users");
                modelAndView.addObject("users", user);
                return modelAndView;
            } else {
                logger.error("User with ID {} not found", id);
                ModelAndView errorModelAndView = new ModelAndView("error");
                errorModelAndView.addObject("errorMessage", "User not found");
                return errorModelAndView;
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching user with ID {}: {}", id, e.getMessage());
            ModelAndView errorModelAndView = new ModelAndView("error");
            errorModelAndView.addObject("errorMessage", "Error occurred while fetching user");
            return errorModelAndView;
        }
    }




    // update the user value by Id
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            Optional<User> optionalUser = userService.getUserById(id);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();

                if (updatedUser.getName() != null) {
                    existingUser.setName(updatedUser.getName());
                }

                if (updatedUser.getEmail() != null) {
                    existingUser.setEmail(updatedUser.getEmail());
                }

                // Update the properties of the existing user
                existingUser.setName(updatedUser.getName());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setLastname(updatedUser.getLastname());
                existingUser.setRole(updatedUser.getRole());

                Address existingAddress = existingUser.getAddress();
                if (existingAddress != null) {
                    // Update the properties of the existing address
                    Address updatedAddress = updatedUser.getAddress();
                    existingAddress.setStreet(updatedAddress.getStreet());
                    existingAddress.setCity(updatedAddress.getCity());
                    existingAddress.setCountry(updatedAddress.getCountry());
                }

                User updatedUserResult = userService.saveUser(existingUser,true);
                logger.info("Updated user with ID: {}", id);
                return ResponseEntity.ok(updatedUserResult);
            } else {
                logger.warn("User with ID {} not found", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating user with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }






    // create user

//post method for form
    @PostMapping("/createuser")
    public ModelAndView saveUser(@ModelAttribute("user") User user) {
        try {
            ModelAndView modelAndView = new ModelAndView();
            if (!userService.isValidEmail(user.getEmail())) {
                String errorMessage = "Invalid email format! Please enter a valid email address.";
                modelAndView.addObject("errorMessage", errorMessage);
                modelAndView.setViewName("signupForm");
                return modelAndView;
            }
            if (userService.isNameExists(user.getName())) {
                String errorMessage = "Name already exists. Please choose a different name.";
                modelAndView.addObject("errorMessage", errorMessage);
                modelAndView.setViewName("signupForm");
                return modelAndView;
            }
            if (userService.isEmailExists(user.getEmail())) {
                String errorMessage = "Email already exists. Please use a different email address.";
                modelAndView.addObject("errorMessage", errorMessage);
                modelAndView.setViewName("signupForm");
                return modelAndView;
            }
            User savedUser = userService.saveUser(user, false);
            modelAndView.addObject("user", savedUser);
            modelAndView.setViewName("signupSuccess");
            return modelAndView;
        } catch (Exception e) {
            ModelAndView errorModelAndView = new ModelAndView("error");
            errorModelAndView.addObject("errorMessage", "Error occurred while saving user: " + e.getMessage());
            return errorModelAndView;
        }
    }

    @GetMapping("/signupForm")
    public ModelAndView showSignupForm() {
        ModelAndView modelAndView = new ModelAndView("signupForm");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }




    @PostMapping("/createauth")
        public ResponseEntity<String> createUser(@RequestBody Auth userEntity3) {
            /*try {*/

            Auth userEncrypt1 = new Auth();
            userEncrypt1.setUsername(userEntity3.getUsername());
            userEncrypt1.setPassword(userEntity3.getPassword());
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            userEncrypt1.setCreatedOn(sdf1.format(new Date()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            userEncrypt1.setModifiedOn(sdf.format(new Date()));

            userEncrypt1.encryptPassword();
            authRepository.save(userEncrypt1);

            logger.info("User added successfully");
            //    logger.info("Saved user: {}", savedUser.getName());
            return ResponseEntity.ok("User added successfully");
        }


    @GetMapping("/getauth")
    public ModelAndView getUserData() {
        try {
            List<Auth> userEncryptList = authRepository.findAll();
            for (Auth userEntity3 : userEncryptList) {
                userEntity3.decryptPassword();
            }
            logger.info("Fetching all users");

            ModelAndView modelAndView = new ModelAndView("auth");
            modelAndView.addObject("users", userEncryptList);
            return modelAndView;
        } catch (Exception e) {
            logger.error("Error occurred while fetching users: {}", e.getMessage());
            ModelAndView errorModelAndView = new ModelAndView("error");
            errorModelAndView.addObject("errorMessage", "Error occurred while fetching users");
            return errorModelAndView;
        }
    }


}
