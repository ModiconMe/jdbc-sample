package io.modicon.app.infrastructure.config;

import io.modicon.app.domain.dao.CustomerDao;
import io.modicon.app.domain.service.CustomerService;
import io.modicon.app.domain.service.CustomerServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CustomerService customerService(CustomerDao customerDao) {
        return new CustomerServiceImpl(customerDao);
    }
}
