package io.modicon.app.domain.service;

import io.modicon.app.application.request.CustomerCreateRequest;
import io.modicon.app.application.request.CustomerUpdateRequest;
import io.modicon.app.application.response.CustomerFetchByEmailResponse;

public interface CustomerService {
    CustomerFetchByEmailResponse findByEmail(String email);
    void addCustomer(CustomerCreateRequest request);
    void updateCustomer(CustomerUpdateRequest customer);
    void deleteCustomer(Long id);
}
