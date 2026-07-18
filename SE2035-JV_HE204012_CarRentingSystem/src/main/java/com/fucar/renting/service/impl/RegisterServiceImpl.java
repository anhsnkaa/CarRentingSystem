package com.fucar.renting.service.impl;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.entity.Account;
import com.fucar.renting.exception.EmailAlreadyExistsException;
import com.fucar.renting.service.AccountService;
import com.fucar.renting.service.CustomerService;
import com.fucar.renting.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final AccountService accountService;
    private final CustomerService customerService;

    @Override
    @Transactional
    public void register(CustomerRegisterRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        if (accountService.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Account savedAccount = accountService.register(request);

        customerService.createForAccount(savedAccount.getId(), request);
    }
}
