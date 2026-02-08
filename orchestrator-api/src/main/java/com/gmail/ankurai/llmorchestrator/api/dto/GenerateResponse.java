package com.gmail.ankurai.llmorchestrator.api.dto;

public record GenerateResponse(
        String requestId,
        String provider,
        String model,
        Output output,
        Usage usage,
        Timing timing
) {
    public record Output(String text, FinishReason finishReason) {}
    public record Usage(long inputTokens, long outputTokens, long totalTokens) {}
    public record Timing(long queuedMs, long inferenceMs, long totalMs) {}
}
