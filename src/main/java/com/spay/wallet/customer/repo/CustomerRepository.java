package com.spay.wallet.customer.repo;

import com.spay.wallet.credentials.Credential;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.customer.entities.Customer;
import com.spay.wallet.customer.entities.CustomerLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(
            "SELECT c FROM Customer c WHERE c.phoneNumber=:phoneNumber or c.email=:email"
    )
    Optional<Customer> selectCustomerByPhoneNumberOrEmail(String phoneNumber, String email);

    @Query("select (count(c) > 0) from Customer c where c.phoneNumber = ?1")
    Boolean existsByPhoneNumber(String phoneNumber);

    @Query("select (count(c) > 0) from Customer c where c.email = ?1")
    Boolean existsByEmail(String email);

    @Query("select c from Customer c where c.email =:source OR c.phoneNumber = :source")
    Optional<Customer> findByEmailAndPhoneNumber(String source);

    @Query("select (count(c) > 0) from Customer c where c.id = ?1 and c.customerLevel = ?2")
    boolean existsByIdAndCustomerLevel(Long id, CustomerLevel customerLevel);

    @Query("select c.profilePath from Customer c where c.id=:id")
    String findCustomerProfile(Long id);

    @Query("select c from Customer c where c.credential.userType= ?2  and (upper(CAST( c.id as string )) like upper(?1) or upper(c.email) like upper(?1) or upper(c.firstName) like upper(?1))")
    List<Customer> findByIdOrEmailOrFirstName(String key,UserType userType);

    @Query("select c.credential from Customer c where c.id = ?1")
    Optional<Credential> selectCustomerAccountCredential(Long customerId);

    @Transactional
    @Modifying
    @Query("update Customer c set c.credential = ?1 where c.id = ?2")
    void updateCredentialById(Credential credential, Long id);

    @Query("select c from Customer c where c.credential.userType = ?1")
    Page<Customer> findByCredential_UserType(UserType userType, Pageable pageable);

    @Query("select count(c) from Customer c where c.credential.userType = ?1")
    long countByUserType(UserType userType);
}