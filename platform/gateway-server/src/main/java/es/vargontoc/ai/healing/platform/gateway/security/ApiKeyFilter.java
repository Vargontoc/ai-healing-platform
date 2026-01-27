package es.vargontoc.ai.healing.platform.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiKeyFilter extends AbstractGatewayFilterFactory<ApiKeyFilter.Config> {

    public ApiKeyFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey("X-API-KEY")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
            if (apiKey == null || config.getValidKeys() == null || !config.getValidKeys().contains(apiKey)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        private List<String> validKeys;

        public List<String> getValidKeys() {
            return validKeys;
        }

        public void setValidKeys(List<String> validKeys) {
            this.validKeys = validKeys;
        }
    }
}
