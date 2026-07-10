package com.fucar.renting.repository;

import com.fucar.renting.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT a from Account a where a.email = :email")
    Account findByEmail(@Param("email") String email);

    @Query("SELECT count(a) > 0 from Account a where a.email = :email")
    boolean existsByEmail(@Param("email") String email);
}
