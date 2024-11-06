package com.spay.wallet.customer.repo;

import com.spay.wallet.credentials.Credential;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.customer.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerUserTypeByPhoneNumberOrEmail() {
        // Given
        Customer customer = getCustomer();
        // When
        underTest.save(customer);
        Optional<Customer> userType = underTest.selectCustomerByPhoneNumberOrEmail(customer.getPhoneNumber(), customer.getEmail());
        // Then
        assertThat(userType).isPresent().hasValueSatisfying(customer1 -> {
            assertThat(customer1.getCredential().getUserType()).isEqualByComparingTo(UserType.CUSTOMER);
        });
    }


    @Test
    void itShouldNotSelectCustomerUserTypeByPhoneNumberOrEmail() {
        // Given
        // When
        Optional<Customer> userType = underTest.selectCustomerByPhoneNumberOrEmail("", "");
        // Then
        assertThat(userType).isNotPresent();
    }

    @Test
    void itShouldSelectByPhoneNumber() {
        // Given
        Customer customer = getCustomer();
        // When
        underTest.save(customer);
        Boolean isExist = underTest.existsByPhoneNumber("+251615084712");
        // Then
        assertThat(isExist).isTrue();

    }

    @Test
    void itShouldSelectByEmail() {
        // Given
        Customer customer = getCustomer();
        // When
        underTest.save(customer);
        Boolean isExist = underTest.existsByEmail("abdirahmanaliyousuf@gmail.com");
        // Then
        assertThat(isExist).isTrue();

    }


    private static Customer getCustomer() {
        Customer  customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Ali");
        customer.setLastName("Ali");
        customer.setMiddleName("Ali");
        customer.setPhoneNumber("+251615084712");
        customer.setEmail("abdirahmanaliyousuf@gmail.com");
        customer.setCountry("Somalia");
        customer.setCountryCode("SO");
        customer.setJoinAt(LocalDateTime.now());
        Credential credential =  new Credential();
        credential.setUsernamePhone(customer.getPhoneNumber());
        credential.setUsernameEmail(customer.getEmail());
        credential.setUserType(UserType.CUSTOMER);
        customer.setCredential(credential);
        return customer;
    }

    @Test
    void itShouldFindByPhoneNumber() {
        // Given
        Customer customer = getCustomer();
        // When
        underTest.save(customer);
        Optional<Customer> customerFound = underTest.findByEmailAndPhoneNumber("+251615084712");
        // Then
        assertThat(customerFound).isPresent();

    }

    @Test
    void itShouldFindByEmail() {
        // Given
        Customer customer = getCustomer();
        // When
        underTest.save(customer);
        Optional<Customer> customerFound = underTest.findByEmailAndPhoneNumber("abdirahmanaliyousuf@gmail.com");
        // Then
        assertThat(customerFound).isPresent();

    }

    @Test
    void itShouldNotFindByEmailOrPhone() {
        // Given
        Customer customer = getCustomer();
        // When
        underTest.save(customer);
        Optional<Customer> customerFound = underTest.findByEmailAndPhoneNumber("abdirahmanaliyousuf@gmail.com +252615084712");
        // Then
        assertThat(customerFound).isNotPresent();

    }
}