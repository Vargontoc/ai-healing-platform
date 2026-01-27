package es.vargontoc.ai.healing.platform.gateway.security;

import es.vargontoc.ai.healing.platform.gateway.config.GatewayLocalSecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LocalEndpointSecurityFilter implements WebFilter {

    private final GatewayLocalSecurityProperties properties;

    public LocalEndpointSecurityFilter(GatewayLocalSecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        System.out.println("Path: " + path);
        System.out.println("Headers: " + exchange.getRequest().getHeaders());
        System.out.println("Valid keys: " + properties.getValidKeys());
        // Only apply to local service endpoint
        if (path.equals("/api/v1/services")) {
            if (!exchange.getRequest().getHeaders().containsKey("X-API-KEY")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
            if (apiKey == null || !properties.getValidKeys().contains(apiKey)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }
}
