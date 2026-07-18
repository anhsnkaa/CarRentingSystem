package com.fucar.renting.service;

import com.fucar.renting.dto.CustomerRegisterRequest;
import com.fucar.renting.dto.CustomerUpdateRequest;
import com.fucar.renting.entity.Customer;
import org.springframework.data.domain.Page;

public interface CustomerService {

    Customer findByAccountId(Integer accountId);

    Customer findById(Integer id);

    Page<Customer> findAll(String keyword, Integer page, Integer size);

    Customer createForAccount(Integer accountId, CustomerRegisterRequest request);

    Customer update(Integer id, CustomerUpdateRequest request);

    void delete(Integer id);

    boolean existsByMobile(String mobile);

    boolean existsByIdentityCard(String identityCard);

    boolean existsByLicenceNumber(String licenceNumber);
}