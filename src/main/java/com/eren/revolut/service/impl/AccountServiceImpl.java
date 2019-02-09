package com.eren.revolut.service.impl;

import com.eren.revolut.model.Account;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.repository.AccountRepository;
import com.eren.revolut.service.AccountService;
import org.jooby.Err;
import org.jooby.Status;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Inject
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account getAccount(UUID id) {
        return accountRepository.getAccount(id).orElseThrow(() -> new Err(Status.NOT_FOUND, "Account not found"));
    }

    @Override
    public List<Account> getAccountsByUser(UUID userId) {
        return accountRepository.getAccountsByUser(userId);
    }

    @Override
    public Account createAccount(AccountRequest accountRequest) {
        accountRequest.validate();
        Account account = Account.builder().id(UUID.randomUUID()).userId(accountRequest.getUserId())
                .currency(accountRequest.getCurrency()).balance(BigDecimal.ZERO).createDate(Instant.now())
                .build();
        accountRepository.createAccount(account);
        return account;
    }

}
