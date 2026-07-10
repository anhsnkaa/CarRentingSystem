package com.fucar.renting.service;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.entity.Customer;

public interface CustomerService {

    Customer findByAccountId(Long accountId);

    Customer createForAccount(Long accountId, CustomerRegisterRequest request);
}
