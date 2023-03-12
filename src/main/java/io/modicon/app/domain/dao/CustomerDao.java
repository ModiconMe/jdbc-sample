package io.modicon.app.domain.dao;

import io.modicon.app.domain.model.Customer;

import java.util.Optional;

public interface CustomerDao {
    Optional<Customer> findByEmail(String email);
    Customer findById(Long id);
    boolean existByEmail(String email);
    boolean existById(Long id);
    void addCustomer(Customer customer);
    void updateCustomer(Customer oldCustomer, Customer newCustomer);
    void deleteCustomer(Long id);
}
