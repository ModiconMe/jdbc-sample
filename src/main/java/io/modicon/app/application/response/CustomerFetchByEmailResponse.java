package io.modicon.app.application.response;

import io.modicon.app.application.dto.CustomerDto;

public record CustomerFetchByEmailResponse(
        CustomerDto customer
) {
}
