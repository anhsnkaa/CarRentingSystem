package com.fucar.renting.service.impl;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.entity.Account;
import com.fucar.renting.repository.AccountRepository;
import com.fucar.renting.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Account register(CustomerRegisterRequest request) {
        Account account = Account.builder()
                .email(request.getEmail())
                .accountName(request.getEmail().split("@")[0])
                .password(passwordEncoder.encode(request.getPassword()))
                .role("CUSTOMER")
                .build();
        return accountRepository.save(account);
    }
}
