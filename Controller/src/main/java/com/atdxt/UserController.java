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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    /*@PostMapping("/login")
    public ModelAndView handleLogin(@RequestParam String username, @RequestParam String password) {
        try {
            // Perform authentication using the provided username and password
            // You can use Spring Security's authenticationManager to authenticate the user.
            // If authentication is successful, set the user's authentication in the security context.

            // Example authentication code (using authenticationManager):
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirect to the appropriate endpoint after successful login
            return new ModelAndView("redirect:/getdata");
        } catch (Exception e) {
            logger.error("Error occurred during login: {}", e.getMessage());
            ModelAndView errorModelAndView = new ModelAndView("error");
            errorModelAndView.addObject("errorMessage", "Error occurred during login");
            return errorModelAndView;
        }
    }*/




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

            // Set createdOn and modifiedOn dates
            Date currentDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            String currentDateTime = formatter.format(currentDate);
            auth.setCreatedOn(currentDateTime);
            auth.setModifiedOn(currentDateTime);

            authRepository.save(auth);

            // Set the Auth object in the User entity
            user.setAuth(auth);
            auth.setUser(user);


            // Check if an image file is uploaded
            if (!image.isEmpty()) {
                logger.info("Image is present!!. Uploading image to S3..."+image);
                // Save the image to AWS S3 and get the image URL
                String imageUrl = userService.uploadImageToS3(image);
                System.out.println("Image url"+imageUrl);
                user.setImg_url(imageUrl);
                logger.info("Image saved to s3 URL = {}",imageUrl);
            }


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

    @GetMapping("/customLogin")
    public ModelAndView showCustomLogin() {
        ModelAndView modelAndView = new ModelAndView("customLogin");
        modelAndView.addObject("user", new Auth());
        return modelAndView;
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
