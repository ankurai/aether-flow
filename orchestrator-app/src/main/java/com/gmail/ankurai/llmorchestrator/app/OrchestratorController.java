package com.gmail.ankurai.llmorchestrator.app;

import com.gmail.ankurai.llmorchestrator.api.LlmProvider;
import com.gmail.ankurai.llmorchestrator.api.dto.*;
import com.gmail.ankurai.llmorchestrator.core.ModelRouter;
import com.gmail.ankurai.llmorchestrator.core.OrchestratorService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${orchestrator.basePath:/internal/ai/v1}")
public class OrchestratorController {

    private final OrchestratorService service;
    private final ModelRouter router;

    public OrchestratorController(OrchestratorService service, ModelRouter router) {
        this.service = service;
        this.router = router;
    }

    @PostMapping("/generate")
    public Mono<GenerateResponse> generate(@Valid @RequestBody GenerateRequest req) {
        return service.generate(req);
    }

    @PostMapping("/code/complete")
    public Mono<CodeCompleteResponse> complete(@Valid @RequestBody CodeCompleteRequest req) {
        return service.complete(req);
    }

    @GetMapping("/health")
    public Mono<HealthResponse> health(@RequestParam(required = false) String provider) {
        LlmProvider chosen = router.choose(provider);
        return chosen.ping()
                .map(ok -> new HealthResponse(ok ? "UP" : "DEGRADED", chosen.id(), ok))
                .onErrorReturn(new HealthResponse("DEGRADED", chosen.id(), false));
    }

    public record HealthResponse(String status, String provider, boolean providerReachable) {}
}
