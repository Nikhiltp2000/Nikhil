package com.atdxt.JdbcConnection;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}

