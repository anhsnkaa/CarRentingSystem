package com.fucar.renting.service.impl;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.dto.CustomerUpdateRequest;
import com.fucar.renting.entity.Customer;
import com.fucar.renting.repository.CustomerRepository;
import com.fucar.renting.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer findByAccountId(Integer accountId) {
        return customerRepository.findByAccountId(accountId);
    }

    @Override
    public Customer findById(Integer id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Customer> findAll(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        if (keyword == null || keyword.isBlank()) {
            return customerRepository.findAll(pageable);
        }
        String kw = "%" + keyword.toLowerCase() + "%";
        return customerRepository.search(kw, pageable);
    }

    @Override
    @Transactional
    public Customer createForAccount(Integer accountId, CustomerRegisterRequest request) {
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

    @Override
    @Transactional
    public Customer update(Integer id, CustomerUpdateRequest request) {
        Customer c = customerRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Customer not found: " + id));
        c.setFullName(request.getFullName());
        c.setMobile(request.getMobile());
        c.setBirthday(request.getBirthday());
        c.setIdentityCard(request.getIdentityCard());
        c.setLicenceNumber(request.getLicenceNumber());
        c.setLicenceDate(request.getLicenceDate());
        return customerRepository.save(c);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        customerRepository.deleteById(id);
    }

    @Override
    public boolean existsByMobile(String mobile) {
        return customerRepository.existsByMobile(mobile);
    }

    @Override
    public boolean existsByIdentityCard(String identityCard) {
        return customerRepository.existsByIdentityCard(identityCard);
    }

    @Override
    public boolean existsByLicenceNumber(String licenceNumber) {
        return customerRepository.existsByLicenceNumber(licenceNumber);
    }
}