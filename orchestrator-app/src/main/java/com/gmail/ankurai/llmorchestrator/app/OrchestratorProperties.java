package com.gmail.ankurai.llmorchestrator.app;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "orchestrator")
public class OrchestratorProperties {
    private String basePath = "/internal/ai/v1";

    public String getBasePath() { return basePath; }
    public void setBasePath(String basePath) { this.basePath = basePath; }
}
