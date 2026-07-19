package com.fucar.renting.repository;

import com.fucar.renting.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("Select c from Customer c where c.accountId = :accountId")
    Customer findByAccountId(@Param("accountId") Integer accountId);

    @Query("select count(c) > 0 from Customer c where c.mobile = :mobile")
    boolean existsByMobile(@Param("mobile") String mobile);

    @Query("select  count(c) > 0 from Customer c where c.identityCard = :identityCard")
    boolean existsByIdentityCard(@Param("identityCard") String identityCard);

    @Query("select count(c) > 0 from Customer c where c.licenceNumber = :licenceNumber")
    boolean existsByLicenceNumber(@Param("licenceNumber") String licenceNumber);

    @Query("SELECT c FROM Customer c WHERE " +
            ":keyword IS NULL OR :keyword = '' OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.mobile) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.identityCard) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Customer> search(@Param("keyword") String keyword, Pageable pageable);
}