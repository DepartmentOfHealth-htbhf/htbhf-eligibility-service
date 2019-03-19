package uk.gov.dhsc.htbhf.eligibility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import uk.gov.dhsc.htbhf.CommonRestConfiguration;

@SuppressWarnings("PMD.UseUtilityClass")
@SpringBootApplication
@Import(CommonRestConfiguration.class)
public class EligibilityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EligibilityServiceApplication.class, args);
    }
}
