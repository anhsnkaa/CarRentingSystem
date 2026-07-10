package com.fucar.renting.service;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.entity.Account;

public interface AccountService {

    Account findByEmail(String email);

    boolean existsByEmail(String email);

    Account register(CustomerRegisterRequest request);
}
