package com.authservice.config;

import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.configuration.ClientConfiguration;
import dev.openfga.sdk.api.configuration.ClientCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenFgaConfig {

    @Value("${openfga.api-url}")
    private String apiUrl;

    @Value("${openfga.store-id}")
    private String storeId;

    @Value("${openfga.model-id:}")
    private String modelId;

    @Value("${openfga.authorization-model-id:}")
    private String authorizationModelId;

    @Bean
    public OpenFgaClient openFgaClient() {
        ClientConfiguration configuration = new ClientConfiguration()
                .apiUrl(apiUrl)
                .storeId(storeId)
                .authorizationModelId(authorizationModelId);

        // Eğer kimlik doğrulama gerekiyorsa
        // configuration.credentials(new ClientCredentials().clientId("...").clientSecret("..."));

        return new OpenFgaClient(configuration);
    }
}
