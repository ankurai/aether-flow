package com.gmail.ankurai.llmorchestrator.app;

import com.gmail.ankurai.llmorchestrator.api.LlmProvider;
import com.gmail.ankurai.llmorchestrator.core.ModelRouter;
import com.gmail.ankurai.llmorchestrator.core.OrchestratorService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
@EnableConfigurationProperties(OrchestratorProperties.class)
public class AppConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ModelRouter modelRouter(List<LlmProvider> providers) {
        return new ModelRouter(providers);
    }

    @Bean
    public OrchestratorService aiService(ModelRouter router) {
        return new OrchestratorService(router);
    }
}
