package com.fucar.renting.service;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.entity.Account;

import java.util.List;

public interface AccountService {

    Account findByAccountName(String accountName);

    Account findByEmail(String email);

    Account findById(Integer id);

    List<Account> findAll();

    boolean existsByEmail(String email);

    boolean existsByAccountName(String accountName);

    Account register(CustomerRegisterRequest request);

    Account createAdmin(String email, String password);

    Account createAccount(String accountName, String email, String password, String role);

    void updateAccount(Integer id, String accountName, String email, String password);

    void deleteById(Integer id);
}