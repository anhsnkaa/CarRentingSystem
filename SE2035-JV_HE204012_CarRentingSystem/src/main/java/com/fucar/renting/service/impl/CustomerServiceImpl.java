package com.fucar.renting.service.impl;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.repository.CustomerRepository;
import com.fucar.renting.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer findByAccountId(Long accountId) {
        return customerRepository.findByAccountId(accountId);
    }

    @Override
    @Transactional
    public Customer createForAccount(Long accountId, CustomerRegisterRequest request) {
        Customer customer = Customer.builder()
                .fullName(request.getFullName())
                .mobile(request.getMobile())
                .birthday(request.getBirthday())
                .identityCard(request.getIdentityCard())
                .licenceNumber(request.getLicenceNumber())
                .licenceDate(request.getLicenceDate())
                .accountId(accountId)
                .build();
        return customerRepository.save(customer);
    }
}
