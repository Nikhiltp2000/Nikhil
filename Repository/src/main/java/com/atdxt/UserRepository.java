package com.atdxt;

import com.atdxt.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);
    boolean existsByEmail(String email);

//    @Query("SELECT u FROM User u LEFT JOIN FETCH u.address")
//    List<User> findAllFetchAddress();

    Optional<User> findByEmail(String email);
}


