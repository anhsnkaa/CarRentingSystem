package com.fucar.renting.service.impl;

import com.fucar.renting.dto.AdminCreateAccountRequest;
import com.fucar.renting.dto.AdminUpdateAccountRequest;
import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.dto.CustomerUpdateRequest;
import com.fucar.renting.entity.Account;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.exception.EmailAlreadyExistsException;
import com.fucar.renting.service.AccountService;
import com.fucar.renting.service.AdminAccountService;
import com.fucar.renting.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminAccountServiceImpl implements AdminAccountService {

    private final AccountService accountService;
    private final CustomerService customerService;

    @Override
    @Transactional
    public Integer createCustomerAccount(AdminCreateAccountRequest request) {
        if (accountService.existsByAccountName(request.getAccountName())) {
            throw new IllegalArgumentException("Account name already taken");
        }
        if (accountService.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        if (customerService.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException("Mobile already registered");
        }
        if (customerService.existsByIdentityCard(request.getIdentityCard())) {
            throw new IllegalArgumentException("Identity card already registered");
        }
        if (customerService.existsByLicenceNumber(request.getLicenceNumber())) {
            throw new IllegalArgumentException("Licence number already registered");
        }

        CustomerRegisterRequest register = CustomerRegisterRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .mobile(request.getMobile())
                .birthday(request.getBirthday())
                .identityCard(request.getIdentityCard())
                .licenceNumber(request.getLicenceNumber())
                .licenceDate(request.getLicenceDate())
                .build();

        Account savedAccount = accountService.createAccount(
                request.getAccountName(),
                request.getEmail(),
                request.getPassword(),
                "CUSTOMER");
        customerService.createForAccount(savedAccount.getId(), register);
        return savedAccount.getId();
    }

    @Override
    @Transactional
    public void updateCustomerAndAccount(Integer customerId, Integer accountId, AdminUpdateAccountRequest request) {
        Account currentAccount = accountService.findById(accountId);
        if (currentAccount == null) {
            throw new IllegalArgumentException("Account not found");
        }

        if (!currentAccount.getAccountName().equals(request.getAccountName())
                && accountService.existsByAccountName(request.getAccountName())) {
            throw new IllegalArgumentException("Account name already taken");
        }
        if (!currentAccount.getEmail().equals(request.getEmail())
                && accountService.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Customer currentCustomer = customerService.findById(customerId);
        if (currentCustomer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        if (!currentCustomer.getMobile().equals(request.getMobile())
                && customerService.existsByMobile(request.getMobile())) {
            throw new IllegalArgumentException("Mobile already registered");
        }
        if (!currentCustomer.getIdentityCard().equals(request.getIdentityCard())
                && customerService.existsByIdentityCard(request.getIdentityCard())) {
            throw new IllegalArgumentException("Identity card already registered");
        }
        if (!currentCustomer.getLicenceNumber().equals(request.getLicenceNumber())
                && customerService.existsByLicenceNumber(request.getLicenceNumber())) {
            throw new IllegalArgumentException("Licence number already registered");
        }

        accountService.updateAccount(accountId, request.getAccountName(), request.getEmail(), request.getPassword());

        CustomerUpdateRequest customerUpdate = CustomerUpdateRequest.builder()
                .fullName(request.getFullName())
                .mobile(request.getMobile())
                .birthday(request.getBirthday())
                .identityCard(request.getIdentityCard())
                .licenceNumber(request.getLicenceNumber())
                .licenceDate(request.getLicenceDate())
                .build();
        customerService.update(customerId, customerUpdate);
    }
}
