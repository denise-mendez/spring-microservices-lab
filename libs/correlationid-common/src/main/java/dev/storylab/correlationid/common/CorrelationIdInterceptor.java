package dev.storylab.correlationid.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class CorrelationIdInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(CorrelationIdInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String correlationId = MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY);

        if (correlationId != null || correlationId.isBlank()) {
            request.getHeaders()
                    .set(CorrelationIdConstants.CORRELATION_ID_HEADER, correlationId);

            log.info("Proagating correlation ID to {}", request.getURI());
        }

        return execution.execute(request, body);
    }
}
