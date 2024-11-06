package com.spay.wallet.customerservice.services;
import com.spay.wallet.customerservice.common.exceptions.ApiException;
import com.spay.wallet.customerservice.repo.CustomerRepository1;
import com.spay.wallet.customerservice.reqres.CustomerDetailResponse1;
import com.spay.wallet.customerservice.entities.Customer1;
import com.spay.wallet.customerservice.utilities.Mapping1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService1 {
    private final CustomerRepository1 customerRepository;


    public CustomerDetailResponse1 getCustomerDetails(Long customerId){
         var customer = getCustomer(customerId);
         return Mapping1.mapCustomerDetail(customer);
    }

    private Customer1 getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException("Customer not found", HttpStatus.NOT_FOUND));
    }


}
