package uk.gov.dhsc.htbhf.eligibility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.dhsc.htbhf.CommonRestConfiguration;

@SuppressWarnings("PMD.UseUtilityClass")
@SpringBootApplication
@EnableSwagger2
@EnableAsync
@Import(CommonRestConfiguration.class)
public class EligibilityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EligibilityServiceApplication.class, args);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setTaskDecorator(new ContextCopyingDecorator());
        return executor;
    }
}
