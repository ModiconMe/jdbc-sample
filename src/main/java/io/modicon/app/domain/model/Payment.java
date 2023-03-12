package io.modicon.app.domain.model;

import lombok.Builder;
import lombok.ToString;

@ToString
@Builder(toBuilder = true)
public class Payment {
    private final Long id;
    private final Long customerId;
    private final Long amount;
}
