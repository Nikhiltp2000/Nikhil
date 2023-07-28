package com.atdxt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // You can add custom query methods or use the default methods provided by JpaRepository

    List<Address> findByUser_Id(Long userId);
}

