package com.findmypet.config.dataportal;

import com.findmypet.dataportal.adaptor.client.LocalCodeClient;
import com.findmypet.dataportal.api.LocalCodePort;
import com.findmypet.dataportal.core.ApiClient;
import com.findmypet.dataportal.core.DataPortalProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DataPortalConfig {

    @Bean
    public DataPortalProperties dataPortalProperties(
            @Value("${findmypet.dataportal.base-url}") String baseUrl,
            @Value("${findmypet.dataportal.service-key}") String serviceKey,
            @Value("${findmypet.dataportal.use-json:true}") boolean useJson,
            @Value("${findmypet.dataportal.connect-timeout-ms:3000}") long connectMs,
            @Value("${findmypet.dataportal.read-timeout-ms:5000}") long readMs
    ) {
        return DataPortalProperties.builder()
                .baseUrl(baseUrl)
                .serviceKey(serviceKey)
                .useJson(useJson)
                .connectTimeout(Duration.ofMillis(connectMs))
                .readTimeout(Duration.ofMillis(readMs))
                .build();
    }

    @Bean
    public ApiClient dataPortalApiClient(DataPortalProperties props) {
        return new ApiClient(props);
    }

    @Bean
    public LocalCodePort localCodePort(ApiClient apiClient) {
        return new LocalCodeClient(apiClient);
    }
}