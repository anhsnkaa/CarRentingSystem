package com.fucar.renting.repository;

import com.fucar.renting.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("Select c from Customer c where c.accountId = :accountId")
    Customer findByAccountId(@Param("accountId") Long AccountId);

    @Query("select count(c) > 0 from Customer c where c.mobile = :mobile")
    boolean existsByMobile(@Param("mobile") String mobile);

    @Query("select  count(c) > 0 from Customer c where c.identityCard = :identityCard")
    boolean existsByIdentityCard(@Param("identityCard") String identityCard);

    @Query("select count(c) > 0 from Customer c where c.licenceNumber =: licenceNumber")
    boolean existsByLicenceNumber(@Param("licenceNumber") String licenceNumber);
}
