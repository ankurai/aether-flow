package com.gmail.ankurai.llmorchestrator.api.dto;

import jakarta.validation.constraints.NotBlank;

public record Message(
        @NotBlank String role,
        @NotBlank String content
) {}
