package com.spay.wallet.customer.controller;

import com.spay.wallet.common.CustomPage;
import com.spay.wallet.customer.reqRes.CustomerDetailResponse;
import com.spay.wallet.customer.reqRes.CustomerRegistrationRequest;
import com.spay.wallet.customer.reqRes.CustomerRegistrationResponse;
import com.spay.wallet.customer.services.AgentService;
import com.spay.wallet.customer.services.CustomerRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@PreAuthorize("hasAuthority('ADMIN') || hasAuthority('DEV')")
@RestController
@RequestMapping("/api/v1/admin-agent")
@RequiredArgsConstructor
public class AdminAgentController {
    private final AgentService agentService;
    private final CustomerRegistrationService customerRegistrationService;

    @GetMapping("/count-agents")
    public ResponseEntity<Map<String,Object>> countCustomers(){
        var totalCustomers= agentService.countAgents();
        return ResponseEntity.ok(Map.of("totalAgents",totalCustomers));
    }

    @GetMapping("/agents")
    public ResponseEntity<CustomPage<CustomerDetailResponse>> getAgents(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(required = false, defaultValue = "30") Integer size){
        return ResponseEntity.ok(agentService.getAgents(page,size));
    }

    @GetMapping("/search-agent")
    public ResponseEntity<List<CustomerDetailResponse>> searchAgents(@RequestParam String key){
        return ResponseEntity.ok(agentService.searchAgent(key));
    }

    @PostMapping("/create")
    public ResponseEntity<CustomerRegistrationResponse> registerNewAgent(@Valid @RequestBody CustomerRegistrationRequest request){
        var response =  customerRegistrationService.registerNewAgent(request);
        return ResponseEntity.ok(response);
    }

}
