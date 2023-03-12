package io.modicon.app.domain.model;

import io.modicon.app.application.dto.CustomerDto;
import io.modicon.app.application.request.CustomerCreateRequest;
import io.modicon.app.application.request.CustomerUpdateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@ToString
@Getter
@Builder(toBuilder = true)
public class Customer {
    @With private final Long id;
    private final String email;
    private final String name;
    private final Integer age;
    private final LocalDateTime createdAt;
    private final List<Payment> payments;

    public CustomerDto toDto() {
        return new CustomerDtoMapper().apply(this);
    }

    public static Customer fromCreateRequest(CustomerCreateRequest request) {
        return Customer.builder()
                .email(request.email())
                .age(request.age())
                .name(request.name())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Customer fromUpdateRequest(CustomerUpdateRequest request) {
        return Customer.builder()
                .email(request.email())
                .age(request.age())
                .name(request.name())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static class CustomerDtoMapper implements Function<Customer, CustomerDto> {
        @Override
        public CustomerDto apply(Customer customer) {
            return CustomerDto.builder()
                    .email(customer.email)
                    .name(customer.name)
                    .age(customer.age)
                    .createdAt(customer.createdAt)
                    .build();
        }
    }
}
