package com.gmail.ankurai.llmorchestrator.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CodeCompleteRequest(
        String requestId,
        @NotBlank String language,
        @NotNull Context context,
        Controls controls,
        Constraints constraints,
        Routing routing
) {
    public record Context(@NotBlank String prefix, String suffix, String filePath) {}
    public record Controls(Double creativity) {}
    public record Constraints(Integer maxOutputTokens, Integer maxLatencyMs) {}
    public record Routing(String preference, String providerHint, String modelHint) {}
}
