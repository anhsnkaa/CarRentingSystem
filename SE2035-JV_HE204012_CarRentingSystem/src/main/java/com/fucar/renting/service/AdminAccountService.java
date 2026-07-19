package com.fucar.renting.service;

import com.fucar.renting.dto.AdminCreateAccountRequest;
import com.fucar.renting.dto.AdminUpdateAccountRequest;

public interface AdminAccountService {

    Integer createCustomerAccount(AdminCreateAccountRequest request);

    void updateCustomerAndAccount(Integer customerId, Integer accountId, AdminUpdateAccountRequest request);
}
