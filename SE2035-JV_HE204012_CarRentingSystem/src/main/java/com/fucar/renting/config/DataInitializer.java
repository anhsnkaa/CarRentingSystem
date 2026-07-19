package com.fucar.renting.config;

import com.fucar.renting.entity.Account;
import com.fucar.renting.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL    = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";

    private final AccountService accountService;

    @Override
    @Transactional
    public void run(String... args) {
        Account byName = accountService.findByAccountName(ADMIN_USERNAME);
        if (byName != null) {
            accountService.deleteById(byName.getId());
        }
        Account byEmail = accountService.findByEmail(ADMIN_EMAIL);
        if (byEmail != null) {
            accountService.deleteById(byEmail.getId());
        }
        accountService.createAdmin(ADMIN_EMAIL, ADMIN_PASSWORD);
    }
}
