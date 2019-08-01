package uk.gov.dhsc.htbhf.eligibility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import uk.gov.dhsc.htbhf.CommonRestConfiguration;
import uk.gov.dhsc.htbhf.logging.EventLogger;
import uk.gov.dhsc.htbhf.logging.LoggingConfiguration;
import uk.gov.dhsc.htbhf.logging.event.ApplicationStartedEvent;


@SuppressWarnings("PMD.UseUtilityClass")
@SpringBootApplication
@EnableSwagger2
@EnableAsync
@Import({CommonRestConfiguration.class, LoggingConfiguration.class})
@Slf4j
public class EligibilityServiceApplication {

    @Value("${app.version:}") // use APP_VERSION env variable if available, otherwise give no version info
    private String appVersion;

    @Value("${instance.index:}") // use INSTANCE_INDEX env variable if available, otherwise give no index info
    private String instanceIndex;

    @Value("${vcap.application.application_id:}") // the id of the application as provided by cf
    private String applicationId;

    @Autowired
    private EventLogger eventLogger;

    public static void main(String[] args) {
        SpringApplication.run(EligibilityServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logAfterStartup() {
        eventLogger.logEvent(ApplicationStartedEvent.builder()
                .applicationId(applicationId)
                .applicationVersion(appVersion)
                .instanceIndex(instanceIndex)
                .build()
        );
    }

    @Bean
    public TaskExecutor taskExecutor(@Value("${taskexecutor.threadpool.min-size}") Integer threadpoolMinSize,
                                     @Value("${taskexecutor.threadpool.max-size}") Integer threadpoolMaxSize,
                                     ContextCopyingDecorator contextCopyingDecorator) {
        log.info("Creating ThreadPoolTaskExecutor with min pool size {} and max {}", threadpoolMinSize, threadpoolMaxSize);
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadpoolMinSize);
        executor.setMaxPoolSize(threadpoolMaxSize);
        executor.setTaskDecorator(contextCopyingDecorator);
        // set queue size to zero to prevent tasks waiting in the queue before spinning up more threads.
        executor.setQueueCapacity(0);
        return executor;
    }
}
