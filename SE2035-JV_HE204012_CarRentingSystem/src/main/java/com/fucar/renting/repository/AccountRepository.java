package com.fucar.renting.repository;

import com.fucar.renting.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query("SELECT a FROM Account a WHERE a.accountName = :accountName")
    Account findByAccountName(@Param("accountName") String accountName);

    @Query("SELECT count(a) > 0 FROM Account a WHERE a.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT count(a) > 0 FROM Account a WHERE a.accountName = :accountName")
    boolean existsByAccountName(@Param("accountName") String accountName);
}