package com.gmail.ankurai.llmorchestrator.adapters.lmstudio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gmail.ankurai.llmorchestrator.api.LlmProvider;
import com.gmail.ankurai.llmorchestrator.api.dto.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class LmStudioProvider implements LlmProvider {

    private final WebClient webClient;
    private final LmStudioProperties props;

    public LmStudioProvider(WebClient.Builder builder, LmStudioProperties props) {
        this.props = props;
        this.webClient = builder.baseUrl(props.getBaseUrl()).build();
    }

    @Override public String id() { return "lmstudio"; }

    @Override
    public Mono<GenerateResponse> generate(GenerateRequest req) {
        long startNs = System.nanoTime();
        String requestId = normalizeRequestId(req.requestId());

        int maxTokens = (req.constraints() != null && req.constraints().maxOutputTokens() != null)
                ? req.constraints().maxOutputTokens()
                : defaultMaxTokens(req.controls());

        double temperature = (req.controls() != null && req.controls().creativity() != null)
                ? clamp(req.controls().creativity(), 0.0, 2.0)
                : 0.2;

        Map<String, Object> body = Map.of(
                "model", props.getModel(),
                "messages", req.input().messages(),
                "temperature", temperature,
                "max_tokens", maxTokens
        );

        return webClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OpenAiChatResponse.class)
                .timeout(Duration.ofMillis(props.getTimeoutMs()))
                .map(r -> toGenerateResponse(requestId, r, startNs))
                .onErrorResume(ex -> Mono.just(errorGenerateResponse(requestId, startNs, ex)));
    }

    @Override
    public Mono<CodeCompleteResponse> complete(CodeCompleteRequest req) {
        long startNs = System.nanoTime();
        String requestId = normalizeRequestId(req.requestId());

        int maxTokens = (req.constraints() != null && req.constraints().maxOutputTokens() != null)
                ? req.constraints().maxOutputTokens()
                : 200;

        double temperature = (req.controls() != null && req.controls().creativity() != null)
                ? clamp(req.controls().creativity(), 0.0, 2.0)
                : 0.1;

        String suffix = (req.context().suffix() != null) ? req.context().suffix() : "";

        String prompt = """
        You are a code completion engine.
        Return ONLY the code to insert at the cursor.

        LANGUAGE: %s

        PREFIX:
        %s

        SUFFIX:
        %s
        """.formatted(req.language(), req.context().prefix(), suffix);

        Map<String, Object> body = Map.of(
                "model", props.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a precise code completion engine."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", temperature,
                "max_tokens", maxTokens
        );

        return webClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OpenAiChatResponse.class)
                .timeout(Duration.ofMillis(props.getTimeoutMs()))
                .map(r -> toCodeCompleteResponse(requestId, r, startNs))
                .onErrorResume(ex -> Mono.just(errorCodeCompleteResponse(requestId, startNs, ex)));
    }

    @Override
    public Mono<Boolean> ping() {
        return webClient.get()
                .uri("/v1/models")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(Math.min(props.getTimeoutMs(), 5000)))
                .map(resp -> true)
                .onErrorReturn(false);
    }

    // ---------- helpers ----------

    private String normalizeRequestId(String requestId) {
        return (requestId != null && !requestId.isBlank()) ? requestId : UUID.randomUUID().toString();
    }

    private int defaultMaxTokens(GenerateRequest.Controls controls) {
        if (controls == null || controls.verbosity() == null) return 600;
        return switch (controls.verbosity()) {
            case SHORT -> 250;
            case MEDIUM -> 600;
            case LONG -> 1200;
        };
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private FinishReason mapFinishReason(String s) {
        if (s == null) return FinishReason.STOP;
        return switch (s) {
            case "length" -> FinishReason.LENGTH;
            case "stop" -> FinishReason.STOP;
            default -> FinishReason.STOP;
        };
    }

    private GenerateResponse toGenerateResponse(String requestId, OpenAiChatResponse r, long startNs) {
        String text = "";
        FinishReason reason = FinishReason.STOP;

        if (r != null && r.choices != null && !r.choices.isEmpty()) {
            var c = r.choices.get(0);
            if (c.message != null && c.message.content != null) text = c.message.content;
            reason = mapFinishReason(c.finish_reason);
        }

        long totalMs = (System.nanoTime() - startNs) / 1_000_000L;
        long inTokens = (r != null && r.usage != null) ? r.usage.prompt_tokens : 0;
        long outTokens = (r != null && r.usage != null) ? r.usage.completion_tokens : 0;
        long totalTokens = (r != null && r.usage != null) ? r.usage.total_tokens : (inTokens + outTokens);

        return new GenerateResponse(
                requestId, id(), props.getModel(),
                new GenerateResponse.Output(text, reason),
                new GenerateResponse.Usage(inTokens, outTokens, totalTokens),
                new GenerateResponse.Timing(0, totalMs, totalMs)
        );
    }

    private CodeCompleteResponse toCodeCompleteResponse(String requestId, OpenAiChatResponse r, long startNs) {
        String completion = "";
        FinishReason reason = FinishReason.STOP;

        if (r != null && r.choices != null && !r.choices.isEmpty()) {
            var c = r.choices.get(0);
            if (c.message != null && c.message.content != null) completion = c.message.content;
            reason = mapFinishReason(c.finish_reason);
        }

        long totalMs = (System.nanoTime() - startNs) / 1_000_000L;
        long inTokens = (r != null && r.usage != null) ? r.usage.prompt_tokens : 0;
        long outTokens = (r != null && r.usage != null) ? r.usage.completion_tokens : 0;
        long totalTokens = (r != null && r.usage != null) ? r.usage.total_tokens : (inTokens + outTokens);

        return new CodeCompleteResponse(
                requestId, id(), props.getModel(),
                new CodeCompleteResponse.Output(completion, reason),
                new GenerateResponse.Usage(inTokens, outTokens, totalTokens),
                new GenerateResponse.Timing(0, totalMs, totalMs)
        );
    }

    private GenerateResponse errorGenerateResponse(String requestId, long startNs, Throwable ex) {
        long totalMs = (System.nanoTime() - startNs) / 1_000_000L;
        return new GenerateResponse(
                requestId, id(), props.getModel(),
                new GenerateResponse.Output("ERROR: " + ex.getMessage(), FinishReason.ERROR),
                new GenerateResponse.Usage(0, 0, 0),
                new GenerateResponse.Timing(0, totalMs, totalMs)
        );
    }

    private CodeCompleteResponse errorCodeCompleteResponse(String requestId, long startNs, Throwable ex) {
        long totalMs = (System.nanoTime() - startNs) / 1_000_000L;
        return new CodeCompleteResponse(
                requestId, id(), props.getModel(),
                new CodeCompleteResponse.Output("ERROR: " + ex.getMessage(), FinishReason.ERROR),
                new GenerateResponse.Usage(0, 0, 0),
                new GenerateResponse.Timing(0, totalMs, totalMs)
        );
    }

    // -------- provider response DTO --------

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class OpenAiChatResponse {
        public List<Choice> choices;
        public Usage usage;

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Choice {
            public MessageObj message;
            public String finish_reason;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class MessageObj {
            public String role;
            public String content;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Usage {
            public long prompt_tokens;
            public long completion_tokens;
            public long total_tokens;
        }
    }
}
