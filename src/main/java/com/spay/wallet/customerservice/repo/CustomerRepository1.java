package com.spay.wallet.customerservice.repo;

import com.spay.wallet.customerservice.entities.Customer1;
import com.spay.wallet.customerservice.entities.CustomerLevel1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository1 extends JpaRepository<Customer1, Long> {

    @Query(
            "SELECT c FROM Customer1 c WHERE c.phoneNumber=:phoneNumber or c.email=:email"
    )
    Optional<Customer1> selectCustomerByPhoneNumberOrEmail(String phoneNumber, String email);

    @Query("select (count(c) > 0) from Customer1 c where c.phoneNumber = ?1")
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("select (count(c) > 0) from Customer1 c where c.email = ?1")
    Boolean existsByEmail(String email);

    @Query("select c from Customer1 c where c.email =:source OR c.phoneNumber = :source")
    Optional<Customer1> findByEmailAndPhoneNumber(String source);

    @Query("select (count(c) > 0) from Customer1 c where c.id = ?1 and c.customerLevel = ?2")
    boolean existsByIdAndCustomerLevel(Long id, CustomerLevel1 customerLevel);

    @Query("select c.profilePath from Customer1 c where c.id=:id")
    String findCustomerProfile(Long id);

}