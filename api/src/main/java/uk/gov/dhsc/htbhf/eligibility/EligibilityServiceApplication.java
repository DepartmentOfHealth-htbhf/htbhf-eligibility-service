package uk.gov.dhsc.htbhf.eligibility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD.UseUtilityClass")
@SpringBootApplication
public class EligibilityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EligibilityServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
