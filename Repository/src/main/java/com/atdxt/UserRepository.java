package com.atdxt;

import com.atdxt.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

//    @Query("SELECT u FROM User u LEFT JOIN FETCH u.address")
//    List<User> findAllFetchAddress();
}


