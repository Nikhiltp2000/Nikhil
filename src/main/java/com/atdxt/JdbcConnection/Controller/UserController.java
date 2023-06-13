package com.atdxt.JdbcConnection.Controller;


import com.atdxt.JdbcConnection.Model.User;
import com.atdxt.JdbcConnection.Model.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserDao userDao;

    @GetMapping("/getdata")
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }


    @PostMapping("/createuser")
    public void createUser(@RequestBody User user) {
        userDao.createUser(user);
        System.out.println("Values added successfully");
    }
}

