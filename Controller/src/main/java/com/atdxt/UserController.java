package com.atdxt;



import com.atdxt.User;
import com.atdxt.UserService;

//import org.apache.logging.log4j.LogManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/getdata")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            logger.info("Returned {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error occurred while fetching users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //get mapping to find user by id
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            Optional<User> optionalUser = userService.getUserById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                logger.info("Found user with ID: {}", id);
                return ResponseEntity.ok(user);
            } else {
                logger.error("User with ID {} not found", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching user with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // update the user value by Id
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            Optional<User> optionalUser = userService.getUserById(id);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();

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

                User updatedUserResult = userService.saveUser(existingUser);
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
        @PostMapping("/createuser")
        public ResponseEntity<Object> saveUser(@RequestBody User user) {
            try {
                if (!userService.isValidEmail(user.getEmail())) {
                    String errorMessage = "Invalid email format!!,Please Enter valid email id";
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("ErrorMessage", errorMessage);
                    errorResponse.put("Input Email", user.getEmail());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
                if (userService.isNameExists(user.getName())) {
                    String errorMessage = "Name already exists";
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("ErrorMessage", errorMessage);
                    errorResponse.put("Input Name", user.getName());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }

                if (userService.isEmailExists(user.getEmail())) {
                    String errorMessage = "Email already exists";
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("ErrorMessage", errorMessage);
                    errorResponse.put("Input Email", user.getEmail());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }

                User savedUser = userService.saveUser(user);
                logger.info("Saved user: {}", savedUser.getName());
               return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
            } catch (Exception e) {
                logger.error("Error occurred while saving user: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
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
        public List<Auth> getUserData() {
            List<Auth> userEncryptList = authRepository.findAll();
            for (Auth userEntity3 : userEncryptList) {
                userEntity3.decryptPassword();
            }
            logger.info("Fetching all users");
            return userEncryptList;
        }
    }
