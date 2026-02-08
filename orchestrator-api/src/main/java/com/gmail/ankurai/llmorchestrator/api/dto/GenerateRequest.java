package com.gmail.ankurai.llmorchestrator.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GenerateRequest(
        String requestId,
        @NotNull TaskType task,
        @NotNull @Valid Input input,
        Controls controls,
        Constraints constraints,
        Routing routing
) {
    public record Input(@NotNull @Valid List<Message> messages) {}
    public record Controls(Double creativity, Verbosity verbosity) {}
    public record Constraints(Integer maxOutputTokens, Integer maxLatencyMs) {}
    public record Routing(String preference, String providerHint, String modelHint) {}
}
