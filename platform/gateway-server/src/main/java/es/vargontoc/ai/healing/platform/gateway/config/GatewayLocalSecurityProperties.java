package es.vargontoc.ai.healing.platform.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "gateway.security.local")
public class GatewayLocalSecurityProperties {

    private List<String> validKeys = new ArrayList<>();

    public List<String> getValidKeys() {
        return validKeys;
    }

    public void setValidKeys(List<String> validKeys) {
        this.validKeys = validKeys;
    }
}
