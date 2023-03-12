package io.modicon.app.domain.service;

import io.modicon.app.application.request.CustomerCreateRequest;
import io.modicon.app.application.request.CustomerUpdateRequest;
import io.modicon.app.application.response.CustomerFetchByEmailResponse;
import io.modicon.app.domain.dao.CustomerDao;
import io.modicon.app.domain.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;

    @Autowired
    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public CustomerFetchByEmailResponse findByEmail(String email) {
        return new CustomerFetchByEmailResponse(customerDao.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("customer not found"))
                .toDto());
    }

    @Override
    public void addCustomer(CustomerCreateRequest request) {
        if (customerDao.existByEmail(request.email()))
            throw new RuntimeException("email already taken");

        customerDao.addCustomer(Customer.fromCreateRequest(request));
    }

    @Override
    public void updateCustomer(CustomerUpdateRequest request) {
        Long id;
        try {
            id = Long.parseLong(request.id());
        } catch (Exception e) {
            throw new RuntimeException("wrong id");
        }

        Optional<Customer> byEmail = customerDao.findByEmail(request.email());
        if (byEmail.isPresent() && !byEmail.get().getId().equals(id))
            throw new RuntimeException("email already taken");

        Customer newCustomer = Customer.fromUpdateRequest(request);
        Customer oldCustomer = customerDao.findById(id);

        customerDao.updateCustomer(oldCustomer, newCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (customerDao.existById(id))
            throw new RuntimeException("email already taken");

        customerDao.deleteCustomer(id);
    }
}
