package uk.gov.dhsc.htbhf.eligibility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static uk.gov.dhsc.htbhf.swagger.SwaggerGenerationUtil.assertSwaggerDocumentationRetrieved;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EligibilityServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EligibilityServiceApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void swaggerDocumentationRetrieved() throws IOException {
        assertSwaggerDocumentationRetrieved(testRestTemplate, port);
    }

}
