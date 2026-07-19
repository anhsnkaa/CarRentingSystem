package com.fucar.renting.service.impl;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.entity.Account;
import com.fucar.renting.repository.AccountRepository;
import com.fucar.renting.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account findByAccountName(String accountName) {
        return accountRepository.findFirstByAccountNameOrderByIdAsc(accountName);
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepository.findFirstByEmailOrderByIdAsc(email);
    }

    @Override
    public Account findById(Integer id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByAccountName(String accountName) {
        return accountRepository.existsByAccountName(accountName);
    }

    @Override
    @Transactional
    public Account register(CustomerRegisterRequest request) {
        Account account = Account.builder()
                .email(request.getEmail())
                .accountName(request.getAccountName())
                .password(request.getPassword())
                .role("CUSTOMER")
                .build();
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account createAdmin(String email, String password) {
        Account account = Account.builder()
                .email(email)
                .accountName(email.split("@")[0])
                .password(password)
                .role("ADMIN")
                .build();
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account createAccount(String accountName, String email, String password, String role) {
        Account account = Account.builder()
                .accountName(accountName)
                .email(email)
                .password(password)
                .role(role)
                .build();
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void updateAccount(Integer id, String accountName, String email, String password) {
        Account account = accountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Account not found: " + id));
        account.setAccountName(accountName);
        account.setEmail(email);
        if (password != null && !password.isBlank()) {
            account.setPassword(password);
        }
        accountRepository.save(account);
    }
}