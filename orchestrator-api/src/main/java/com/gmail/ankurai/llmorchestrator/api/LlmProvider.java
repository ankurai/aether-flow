package com.gmail.ankurai.llmorchestrator.api;

import com.gmail.ankurai.llmorchestrator.api.dto.CodeCompleteRequest;
import com.gmail.ankurai.llmorchestrator.api.dto.CodeCompleteResponse;
import com.gmail.ankurai.llmorchestrator.api.dto.GenerateRequest;
import com.gmail.ankurai.llmorchestrator.api.dto.GenerateResponse;
import reactor.core.publisher.Mono;

public interface LlmProvider {
    String id();
    Mono<GenerateResponse> generate(GenerateRequest req);
    Mono<CodeCompleteResponse> complete(CodeCompleteRequest req);
    Mono<Boolean> ping();
}
