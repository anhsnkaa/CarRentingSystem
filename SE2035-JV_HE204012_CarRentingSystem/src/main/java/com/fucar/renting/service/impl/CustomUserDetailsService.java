package com.fucar.renting.service.impl;

import com.fucar.renting.entity.Account;
import com.fucar.renting.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String accountName) throws UsernameNotFoundException {
        Account account = accountRepository.findFirstByAccountNameOrderByIdAsc(accountName);

        if (account == null) {
            throw new UsernameNotFoundException("Account not found: " + accountName);
        }

        return new CustomUserDetails(account);
    }
}