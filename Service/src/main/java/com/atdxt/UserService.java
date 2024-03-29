package com.atdxt;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;



import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;




@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final AuthRepository authRepository;

    private final S3Client s3Client;

//    private final String awsS3BucketName;

    private final JavaMailSender javaMailSender;



    private PasswordResetTokenRepository passwordResetTokenRepository;

    private  Environment environment;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository, AuthRepository authRepository,S3Client s3Client,PasswordResetTokenRepository passwordResetTokenRepository,JavaMailSender javaMailSender,Environment environment) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.authRepository= authRepository;
        this.s3Client = s3Client;
        this.passwordResetTokenRepository= passwordResetTokenRepository;
        this.javaMailSender=javaMailSender;
        this.environment=environment;


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





//Save user
    public User saveUser(User user, boolean isUpdate,MultipartFile image) throws IOException  {
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
            return saveUserWithNewColumns(user,image);
        } else {
            // Save user without new columns
            return saveUserWithoutNewColumns(user,image);
        }
    }

    private User saveUserWithNewColumns(User user,MultipartFile image)throws IOException {

        // Upload image to AWS S3 and get the image URL
        if (image != null && !image.isEmpty()) {
            logger.info("Image is present!!. Uploading image to S3..."+image);
            String imageUrl = uploadImageToS3(image);
            System.out.println("Image url"+imageUrl);
            user.setImg_url(imageUrl);
            logger.info("image url log"+imageUrl);
        }
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
    private User saveUserWithoutNewColumns(User user,MultipartFile image)throws IOException {

        // Upload image to AWS S3 and get the image URL
        if (image != null && !image.isEmpty()) {
            logger.info("Image is present. Uploading image to S3..."+ image);
            String imageUrl = uploadImageToS3(image);
            user.setImg_url(imageUrl);
        }
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

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    //Generate token
    public String generateResetToken(User user) {

        String resetTokenValue = UUID.randomUUID().toString();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 1); // Set the expiration time (1 hour from now)
        Date expirationDate = cal.getTime();

        PasswordResetToken resetToken = new PasswordResetToken( resetTokenValue,expirationDate);

        resetToken.setUser(user);
      //  resetToken.setToken(resetTokenValue);
        passwordResetTokenRepository.save(resetToken);

        return resetTokenValue;
    }


    //Send reset password link to email
    public void sendPasswordResetEmail(User user, String resetToken) {

        String url = environment.getProperty("app.url");
        System.out.println("Environment :"+ url);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Password Reset");
        message.setText("Click the following link to reset your password:\n"
                +url+ "/reset-password?token=" + resetToken);

        javaMailSender.send(message);
    }


    //update email
    public ResponseEntity<String> updatePassword(String token, String password) {
        logger.info("Updating password for token: {}", token);

        PasswordResetToken emailForget = passwordResetTokenRepository.findByToken(token);
        logger.warn("Invalid or expired token: {}", token);
        if (emailForget == null || emailForget.getExpiryDate().before(new Date())) {
            // Invalid or expired token, return appropriate response
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }

        User user = emailForget.getUser();
        Auth userEncrypt = user.getAuth();

        // Update the user's encrypted password and save it in the database
        userEncrypt.setPassword(password);
        userEncrypt.encryptPassword();
        authRepository.save(userEncrypt);

        logger.info("Password updated successfully for user: {}");

        // Delete the used token from the database
        passwordResetTokenRepository.delete(emailForget);

        logger.info("Password update process completed successfully.");

        // Return success response
        return ResponseEntity.ok("Password updated successfully.");
    }



    @Value("${aws.accessKeyId}")
    private String awsAccessKeyId;
    {
        System.out.println(awsAccessKeyId);
    }

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.Bucket}")
    private String awsS3BucketName;

    public String uploadImageToS3(MultipartFile image) throws IOException {
        try {
            String key = "images/" + image.getOriginalFilename();
            System.out.println("key : " +key);
            //String awsS3BucketName = "dev-bucket-2924";
           // String awsS3BucketName = "prod-bucket-2924";

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsS3BucketName)
                    .key(key)
                    .contentType(image.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(image.getInputStream(), image.getSize()));

            return "https://"+awsS3BucketName+".s3.amazonaws.com/" + key;
        } catch (S3Exception e) {
            e.printStackTrace();
            // Handle the exception accordingly
            throw e;
        }
    }
}