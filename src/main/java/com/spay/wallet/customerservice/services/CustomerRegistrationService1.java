package com.spay.wallet.customerservice.services;

import com.spay.wallet.customerservice.clients.BasicCustomerRegistrationRequest;
import com.spay.wallet.customerservice.clients.CompletedCustomerRegistrationRequest;
import com.spay.wallet.customerservice.clients.CustomerRegistrationResponse;
import com.spay.wallet.customerservice.common.dto.CreateDefaultAccountPayload;
import com.spay.wallet.customerservice.common.exceptions.ApiException;
import com.spay.wallet.customerservice.common.security.UserRole;
import com.spay.wallet.customerservice.entities.Customer1;
import com.spay.wallet.customerservice.entities.CustomerLevel1;
import com.spay.wallet.customerservice.repo.CustomerRepository1;
import com.spay.wallet.customerservice.reqres.CustomerCompleteRegistrationRequest1;
import com.spay.wallet.customerservice.utilities.CustomerIdGenerator1;
import com.spay.wallet.customerservice.utilities.FileStore1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
//import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.http.HttpStatus;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerRegistrationService1 {
    private final CustomerRepository1 customerRepository;
    private final CustomerIdGenerator1 customerIdGenerator;
    private final FileStore1 fileStore;
//    private final KafkaTemplate<String,Object> kafkaTemplate;
    public String registerBasicCustomer(BasicCustomerRegistrationRequest request) {
        var existsByPhoneNumber = customerRepository.existsByPhoneNumber(request.phoneNumber().replace(" ",""));

        if(existsByPhoneNumber)
            throw new ApiException("Phone number is already exists",HttpStatus.BAD_REQUEST);
        var existsByEmail = customerRepository.existsByEmail(request.email().replace(" ",""));
        if(existsByEmail)
            throw new ApiException("Email address is already exists",HttpStatus.BAD_REQUEST);
        var customerId =  customerIdGenerator.generateId();
        var customer =  Customer1.builder().id(customerId)
                .credentialId(request.credentialId()).phoneNumber(request.phoneNumber().trim())
                .email(request.email().trim()).joinAt(LocalDateTime.now()).build();

        customerRepository.save(customer);
        return customerId.toString();
    }

    public String toWelcome(String name){
            return "hi"+name;
    }

    public void uploadProfileImage(String customerId, MultipartFile file){
        if (file == null)
            throw new ApiException("Cannot upload empty profile",HttpStatus.NOT_ACCEPTABLE);
        var customer = getCustomerById(Long.parseLong(customerId));
        var oldImage = customer.getProfilePath();
        var fileName =UUID.randomUUID().toString().concat(".jpg");
        customer.setProfilePath(fileName);
        var resizedProfile = fileStore.resizeImage(file);
        fileStore.storeFile(fileName, resizedProfile);
        customerRepository.save(customer);
        fileStore.deleteFiles(oldImage);
    }

    private Customer1 getCustomerById(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new ApiException("Customer does not exist", HttpStatus.NOT_FOUND));
    }

    public CustomerRegistrationResponse completeCustomerRegistrationRequest(CustomerCompleteRegistrationRequest1 request) {
        customerIdIsNumeric(request.customerId());
        var customer = customerRepository.findById(Long.parseLong(request.customerId())).orElseThrow(()->  new ApiException("Customer does not exist", HttpStatus.NOT_FOUND));
        if(CustomerLevel1.GUEST_CUSTOMER.equals(customer.getCustomerLevel()))
            return new CustomerRegistrationResponse("Customer info is already been provided", customer.getId().toString(),customer.getCredentialId());
        if(customer.getCustomerLevel() != null && List.of(CustomerLevel1.PARTIAL_CUSTOMER,CustomerLevel1.FULL_CUSTOMER).contains(customer.getCustomerLevel()))
            throw new ApiException("Customer is already bean onboarded",HttpStatus.BAD_REQUEST);
        if(!customer.getCredentialId().equals(request.credentialId()))
            throw new ApiException("Invalid credential Id",HttpStatus.NOT_ACCEPTABLE);
        customer.setFirstName(StringUtils.capitalize(request.firstName().trim()));
        customer.setMiddleName(StringUtils.capitalize((request.middleName() == null?"":request.middleName()).trim()));
        customer.setLastName(StringUtils.capitalize(request.lastName()).trim());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setCountry(request.country());
        customer.setCountryCode(request.countryCode());
        customer.setCustomerLevel(CustomerLevel1.GUEST_CUSTOMER);
        customerRepository.save(customer);
        return new CustomerRegistrationResponse("Customer is onboarded successfully", customer.getId().toString(),customer.getCredentialId());
    }

    public void customerRegistrationToPartial(CompletedCustomerRegistrationRequest request) {
        customerIdIsNumeric(request.customerId());
        var customer = customerRepository.findById(Long.parseLong(request.customerId())).orElseThrow(()->  new ApiException("Customer does not exist", HttpStatus.NOT_FOUND));
        customer.setCustomerLevel(CustomerLevel1.PARTIAL_CUSTOMER);
        String fullName = customer.getFirstName() + " " + customer.getMiddleName() + " " + customer.getLastName();
        var walletAccountCreationPayload = new CreateDefaultAccountPayload(fullName, UserRole.CUSTOMER.name(),customer.getPhoneNumber(),request.aPin(),customer.getId().toString());
//        kafkaTemplate.send(CustomTopic.DEFAULT_ACCOUNT_CREATION,walletAccountCreationPayload);
        customerRepository.save(customer);

    }

    private static void customerIdIsNumeric(String id) {
        if(!StringUtils.isNumeric(id))
            throw new ApiException("Customer id must be digits",HttpStatus.BAD_REQUEST);
    }
}
