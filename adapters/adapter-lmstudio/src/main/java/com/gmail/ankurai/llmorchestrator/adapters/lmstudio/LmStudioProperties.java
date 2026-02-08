package com.gmail.ankurai.llmorchestrator.adapters.lmstudio;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "providers.lmstudio")
public class LmStudioProperties {
    private String baseUrl = "http://127.0.0.1:3000";
    private String model = "local-model";
    private long timeoutMs = 20000;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public long getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(long timeoutMs) { this.timeoutMs = timeoutMs; }
}
