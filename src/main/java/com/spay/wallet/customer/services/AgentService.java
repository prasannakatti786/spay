package com.spay.wallet.customer.services;

import com.spay.wallet.common.CustomPage;
import com.spay.wallet.credentials.UserType;
import com.spay.wallet.customer.repo.CustomerRepository;
import com.spay.wallet.customer.reqRes.CustomerDetailResponse;
import com.spay.wallet.customer.utilities.Mapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {
    private final CustomerRepository customerRepository;

    public CustomPage<CustomerDetailResponse> getAgents(Integer page, Integer size) {
       var pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "joinAt");
       var customersPage = customerRepository.findByCredential_UserType(UserType.AGENT,pageRequest).map(Mapping::mapCustomerDetail);
       return new CustomPage<>(customersPage);
    }

    public List<CustomerDetailResponse> searchAgent(String key) {
        return customerRepository.findByIdOrEmailOrFirstName(key,UserType.AGENT).stream().map(Mapping::mapCustomerDetail).toList();
    }

    public Long countAgents() {
        return customerRepository.countByUserType(UserType.AGENT);
    }

}
