package io.modicon.app.application.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CustomerDto(
        String name,
        String email,
        int age,
        LocalDateTime createdAt
) {
}
