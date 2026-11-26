package dev.storylab.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {
    public static final String CORRELATION_ID = "correlationId";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

         final String correlationIdHeader = correlationId;

        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> headers.set(CORRELATION_ID_HEADER, correlationIdHeader))
                .build();

        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(request)
                .build();

        modifiedExchange.getResponse()
                .getHeaders()
                .add(CORRELATION_ID_HEADER, correlationId);

        return chain
                .filter(modifiedExchange)
                .contextWrite(context -> context.put(CORRELATION_ID, correlationIdHeader));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
