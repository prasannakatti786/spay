package com.spay.wallet;

import com.spay.wallet.properies.WalletProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
@OpenAPIDefinition

@SpringBootApplication(
		scanBasePackages = {
				"com.spay.wallet",

		}
)
@EnableConfigurationProperties({
		WalletProperties.class,
})
@EnableAsync

public class WalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletApplication.class, args);
	}

}
