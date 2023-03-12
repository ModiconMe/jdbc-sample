package io.modicon.app.application.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record CustomerCreateRequest(@Email @NotEmpty String email, @NotEmpty String name, @NotEmpty int age) {
}
