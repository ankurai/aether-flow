package com.gmail.ankurai.llmorchestrator.api.dto;

public record CodeCompleteResponse(
        String requestId,
        String provider,
        String model,
        Output output,
        GenerateResponse.Usage usage,
        GenerateResponse.Timing timing
) {
    public record Output(String completion, FinishReason finishReason) {}
}
