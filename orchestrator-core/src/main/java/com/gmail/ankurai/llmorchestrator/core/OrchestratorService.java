package com.gmail.ankurai.llmorchestrator.core;

import com.gmail.ankurai.llmorchestrator.api.dto.CodeCompleteRequest;
import com.gmail.ankurai.llmorchestrator.api.dto.CodeCompleteResponse;
import com.gmail.ankurai.llmorchestrator.api.dto.GenerateRequest;
import com.gmail.ankurai.llmorchestrator.api.dto.GenerateResponse;
import reactor.core.publisher.Mono;

public class OrchestratorService {

    private final ModelRouter router;

    public OrchestratorService(ModelRouter router) {
        this.router = router;
    }

    public Mono<GenerateResponse> generate(GenerateRequest req) {
        String providerHint = (req.routing() != null) ? req.routing().providerHint() : null;
        return router.choose(providerHint).generate(req);
    }

    public Mono<CodeCompleteResponse> complete(CodeCompleteRequest req) {
        String providerHint = (req.routing() != null) ? req.routing().providerHint() : null;
        return router.choose(providerHint).complete(req);
    }
}
