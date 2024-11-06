package com.spay.wallet.config;

import com.spay.wallet.account.services.AccountCreationService;
import com.spay.wallet.account.services.SettlementService;
import com.spay.wallet.admin.service.AdminAccountService;
import com.spay.wallet.properies.WalletProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class WalletConfig implements CommandLineRunner {
    private final WalletProperties properties;
    private final AdminAccountService adminAccountService;
    private final SettlementService settlementService;


    @Override
    public void run(String... args) throws Exception {
        var account =  properties.getDevAccount();
        adminAccountService.createDevAccount(account.getName(), account.getPassword(),  account.getEmail(),account.getPhoneNumber());
        settlementService.createSettlementAccount();
    }
}
