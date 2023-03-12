package io.modicon.app.application.web;

import io.modicon.app.application.dto.CustomerDto;
import io.modicon.app.application.request.CustomerCreateRequest;
import io.modicon.app.application.request.CustomerUpdateRequest;
import io.modicon.app.application.response.CustomerFetchByEmailResponse;
import io.modicon.app.domain.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{email}")
    public CustomerFetchByEmailResponse fetchCustomerByEmail(@PathVariable String email) {
        return customerService.findByEmail(email);
    }

    @PostMapping
    public void createCustomer(@RequestBody CustomerCreateRequest request) {
        customerService.addCustomer(request);
    }

    @PutMapping("/{id}")
    public void updateCustomer(@RequestBody CustomerUpdateRequest request, @PathVariable String id) {
        customerService.updateCustomer(request.withId(id));
    }

    @DeleteMapping("/{id}")
    public void fetchCustomerByEmail(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
