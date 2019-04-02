package uk.gov.dhsc.htbhf.eligibility;

import org.springframework.beans.factory.annotation.Value;
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
    public TaskExecutor taskExecutor(@Value("${taskexecutor.threadpool.min-size}") Integer threadpoolMinSize,
                                     @Value("${taskexecutor.threadpool.max-size}") Integer threadpoolMaxSize) {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadpoolMinSize);
        executor.setMaxPoolSize(threadpoolMaxSize);
        executor.setTaskDecorator(new ContextCopyingDecorator());
        return executor;
    }
}
