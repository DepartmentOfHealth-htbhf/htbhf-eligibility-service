package uk.gov.dhsc.htbhf.eligibility;

import lombok.AllArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import uk.gov.dhsc.htbhf.requestcontext.RequestContext;

import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Copies over request attributes to allow the use of request scoped beans in async methods.
 */
@Component
@AllArgsConstructor
public class ContextCopyingDecorator implements TaskDecorator {

    private uk.gov.dhsc.htbhf.requestcontext.RequestContextHolder requestContextHolder;

    @Nonnull
    @Override
    public Runnable decorate(@Nonnull Runnable runnable) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        RequestContext requestContext = requestContextHolder.get();

        return () -> {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                MDC.setContextMap(contextMap);
                requestContextHolder.set(requestContext);
                runnable.run();
            } finally {
                MDC.clear();
                RequestContextHolder.resetRequestAttributes();
                requestContextHolder.clear();
            }
        };
    }
}
