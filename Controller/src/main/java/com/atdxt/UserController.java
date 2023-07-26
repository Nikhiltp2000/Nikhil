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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
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


    // Fetch all data from the database, Return values in tabular format
  /*  @GetMapping("/getdata")
    public ModelAndView getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            logger.info("Returned {} users", users.size());

            // Decrypt the passwords
            for (User user : users) {
                Auth auth = user.getAuth();
                String decodedPassword = new String(Base64.getDecoder().decode(auth.getPassword()));
                auth.setPassword(decodedPassword);
            }

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
*/


    @GetMapping("/getdata")
    public ModelAndView getAllUsers(ModelAndView model, Principal principal) {
        try {
            logger.info("Fetching all users");
            String username = principal.getName();

            if (username.equals("Admin")) {
                List<User> users = userService.getAllUsers();

                model.setViewName("users");
                model.addObject("users", users);
                return model;
            } else {

                Auth auth = userService.getUserByUsername(username);

                if (auth != null) {
                    List<User> users = new ArrayList<>();
                    users.add(auth.getUser());  // Access the associated User object using getUser()
                    model.setViewName("users");
                    model.addObject("users", users);
                    return model;
                }
            }

        } catch (Exception e) {
            logger.error("Error occurred while fetching users: {}", e.getMessage());
            ModelAndView errorModelAndView = new ModelAndView("error");
            errorModelAndView.addObject("errorMessage", "Error occurred while fetching users");
            return errorModelAndView;
        }
        return null;
    }











    @GetMapping("/")
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView("home");
        return modelAndView;
    }




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
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser,@RequestParam("image") MultipartFile image) {
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

                User updatedUserResult = userService.saveUser(existingUser,true,image);
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

   /* @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            Optional<User> optionalUser = userService.getUserById(id);
            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();

                // Update the properties of the existing user
                if (updatedUser.getName() != null) {
                    existingUser.setName(updatedUser.getName());
                }
                if (updatedUser.getEmail() != null) {
                    existingUser.setEmail(updatedUser.getEmail());
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
    }*/




    // create user
       /* @PostMapping("/createuser")
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

                User savedUser = userService.saveUser(user,false);
                logger.info("Saved user: {}", savedUser.getName());
               return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
            } catch (Exception e) {
                logger.error("Error occurred while saving user: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
       }*/
//post method for form (create user)
    @PostMapping("/createuser")
    public ModelAndView saveUser(@ModelAttribute("user") User user, @RequestParam("confirmPassword") String confirmPassword, @RequestParam("image") MultipartFile image) {
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

            // Check if the password and confirm password matches
            if (!user.getAuth().getPassword().equals(confirmPassword)) {
                String errorMessage = "Password and confirm password do not match.";
                modelAndView.addObject("errorMessage", errorMessage);
                modelAndView.setViewName("signupForm");
                return modelAndView;
            }

            // Set the username and password in the Auth entity
            Auth auth = new Auth();
            auth.setUsername(user.getAuth().getUsername());
            auth.setPassword(user.getAuth().getPassword());
            auth.encryptPassword();

            // Encode the password using BCryptPasswordEncoder
           /* String encodedPassword = passwordEncoder.encode(user.getAuth().getPassword());
            auth.setPassword(encodedPassword);*/

            // Encode the password in Base64
           /* String encodedPassword = Base64.getEncoder().encodeToString(user.getAuth().getPassword().getBytes());
            auth.setPassword(encodedPassword);*/


            // Set createdOn and modifiedOn dates
            Date currentDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            String currentDateTime = formatter.format(currentDate);
            auth.setCreatedOn(currentDateTime);
            auth.setModifiedOn(currentDateTime);

            authRepository.save(auth);

            // Set the Auth object in the User entity
            user.setAuth(auth);


            // Check if an image file is uploaded
           /* if (!image.isEmpty()) {
                // Save the image to AWS S3 and get the image URL
                String imageUrl = userService.uploadImageToS3(image);
                user.setImg_url(imageUrl);
                logger.info("Image saved to s3 URL = {}",imageUrl);
            }*/

            User savedUser = userService.saveUser(user, false,image);
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



// create username and password (commented for now as username and password is now storing through the signUp form)
  /*  @PostMapping("/createauth")
        public ResponseEntity<String> createUser(@RequestBody Auth userEntity3) {
            *//*try {*//*

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
        }*/

    /*    @GetMapping("/getauth")
        public List<Auth> getUserData() {
            List<Auth> userEncryptList = authRepository.findAll();
            for (Auth userEntity3 : userEncryptList) {
                userEntity3.decryptPassword();
            }
            logger.info("Fetching all users");
            return userEncryptList;
        }*/

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
