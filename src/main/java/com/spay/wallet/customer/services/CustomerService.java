package com.spay.wallet.customer.services;

import com.spay.wallet.account.utilities.FormatDateAndTime;
import com.spay.wallet.common.CustomPage;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.customer.entities.Customer;
import com.spay.wallet.customer.repo.CustomerRepository;
import com.spay.wallet.customer.reqRes.CustomerAdminDetailResponse;
import com.spay.wallet.customer.reqRes.CustomerDetailResponse;
import com.spay.wallet.customer.utilities.Mapping;
import com.spay.wallet.exections.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;


    public CustomerDetailResponse getCustomerDetails(Long customerId) {
        var customer = getCustomer(customerId);
        return Mapping.mapCustomerDetail(customer);
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));
    }



    public String getCustomerProfile(Long id) {
        return customerRepository.findCustomerProfile(id);
    }

    public CustomPage<CustomerDetailResponse> getCustomers(Integer page, Integer size) {
        var customersPage = customerRepository.findByCredential_UserType(UserType.CUSTOMER,PageRequest.of(page, size, Sort.Direction.DESC, "joinAt"))
                .map(Mapping::mapCustomerDetail);
        return new CustomPage<>(customersPage);
    }

    public List<CustomerDetailResponse> searchCustomer(String key) {
        return customerRepository.findByIdOrEmailOrFirstName(key,UserType.CUSTOMER).stream().map(Mapping::mapCustomerDetail).toList();
    }

    public CustomerAdminDetailResponse getAdminCustomerDetails(Long customerId) {
        var customer = getCustomer(customerId);
        return new CustomerAdminDetailResponse(customer);
    }

    public Boolean unLockOrLockCustomerAccount(Long customerId) {
        var credential = customerRepository.selectCustomerAccountCredential(customerId).orElseThrow(() -> new ApiException("Customer lock state not found", HttpStatus.NOT_FOUND));
        credential.setIsLocked(credential.getIsLocked()==null?Boolean.FALSE:!credential.getIsLocked());
        customerRepository.updateCredentialById(credential, customerId);
        return credential.getIsLocked();
    }

    public Boolean enableOrDisableCustomerAccount(Long customerId) {
        var credential = customerRepository.selectCustomerAccountCredential(customerId).orElseThrow(() -> new ApiException("Customer lock state not found", HttpStatus.NOT_FOUND));
        credential.setIsEnabled(credential.getIsEnabled() == null?Boolean.FALSE:!credential.getIsEnabled());
        customerRepository.updateCredentialById(credential, customerId);
        return credential.getIsEnabled();
    }

    public Long countCustomers() {
        return customerRepository.countByUserType(UserType.CUSTOMER);
    }


}
