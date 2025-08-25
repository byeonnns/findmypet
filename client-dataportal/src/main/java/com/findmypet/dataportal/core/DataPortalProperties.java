package com.findmypet.dataportal.core;

import java.time.Duration;

public class DataPortalProperties {
    private final String baseUrl;       // ex) https://apis.data.go.kr
    private final String serviceKey;    // Decoding 키 (이미 인코딩된 값 그대로)
    private final boolean useJson;      // true면 _type=json 자동 부착
    private final Duration connectTimeout;
    private final Duration readTimeout;

    public DataPortalProperties(String baseUrl, String serviceKey, boolean useJson,
                                Duration connectTimeout, Duration readTimeout) {
        this.baseUrl = baseUrl;
        this.serviceKey = serviceKey;
        this.useJson = useJson;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public String getBaseUrl() { return baseUrl; }
    public String getServiceKey() { return serviceKey; }
    public boolean isUseJson() { return useJson; }
    public Duration getConnectTimeout() { return connectTimeout; }
    public Duration getReadTimeout() { return readTimeout; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String baseUrl = "https://apis.data.go.kr";
        private String serviceKey;
        private boolean useJson = true;
        private Duration connectTimeout = Duration.ofSeconds(3);
        private Duration readTimeout = Duration.ofSeconds(5);
        public Builder baseUrl(String v){ this.baseUrl=v; return this; }
        public Builder serviceKey(String v){ this.serviceKey=v; return this; }
        public Builder useJson(boolean v){ this.useJson=v; return this; }
        public Builder connectTimeout(Duration v){ this.connectTimeout=v; return this; }
        public Builder readTimeout(Duration v){ this.readTimeout=v; return this; }
        public DataPortalProperties build(){
            return new DataPortalProperties(baseUrl, serviceKey, useJson, connectTimeout, readTimeout);
        }
    }
}
