package com.atdxt.JpaConnection.Repository;

// UserRepository.java
import com.atdxt.JpaConnection.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}


