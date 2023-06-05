package com.atdxt.JdbcConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM Employee";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String email = resultSet.getString("email");
            return new User(id, name,email);
        });
    }
}

