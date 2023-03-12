package io.modicon.app.application.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Null;
import lombok.With;

public record CustomerUpdateRequest(@Email @Null String email, @Null String name, @Null int age, @With String id) {
}
