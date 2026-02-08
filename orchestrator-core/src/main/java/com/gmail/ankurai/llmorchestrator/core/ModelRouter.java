package com.gmail.ankurai.llmorchestrator.core;

import com.gmail.ankurai.llmorchestrator.api.LlmProvider;

import java.util.List;

public class ModelRouter {
    private final List<LlmProvider> providers;

    public ModelRouter(List<LlmProvider> providers) {
        if (providers == null || providers.isEmpty()) {
            throw new IllegalStateException("No LlmProvider beans registered");
        }
        this.providers = providers;
    }

    public LlmProvider choose(String providerHint) {
        if (providerHint != null && !providerHint.isBlank()) {
            return providers.stream()
                    .filter(p -> p.id().equalsIgnoreCase(providerHint))
                    .findFirst()
                    .orElse(providers.get(0));
        }
        return providers.get(0);
    }
}
