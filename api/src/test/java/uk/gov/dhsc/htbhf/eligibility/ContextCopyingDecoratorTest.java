package uk.gov.dhsc.htbhf.eligibility;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.dhsc.htbhf.requestcontext.RequestContext;
import uk.gov.dhsc.htbhf.requestcontext.RequestContextHolder;

import static java.util.Collections.emptyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ContextCopyingDecoratorTest {

    @MockBean
    private RequestContextHolder requestContextHolder;
    @Mock
    private RequestContext requestContext;
    @Mock
    private Runnable runnable;

    @Autowired
    private ContextCopyingDecorator decorator;

    @Test
    void shouldCopyRequestContext() {
        MDC.setContextMap(emptyMap());
        given(requestContextHolder.get()).willReturn(requestContext);

        decorator.decorate(runnable).run();

        verify(runnable).run();
        verify(requestContextHolder).set(requestContext);
    }

}
