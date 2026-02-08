package com.gmail.ankurai.llmorchestrator.adapters.lmstudio;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LmStudioProperties.class)
public class LmStudioAdapterConfig {
    // Just enables properties binding; provider is a @Component
}
