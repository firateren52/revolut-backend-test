package com.eren.revolut.service.impl;

import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.repository.AccountRepository;
import com.eren.revolut.repository.AccountTransactionRepository;
import com.eren.revolut.service.AccountService;
import org.jooby.Err;
import org.jooby.Status;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Singleton
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final AccountTransactionRepository accountTransactionRepository;

    @Inject
    public AccountServiceImpl(AccountRepository accountRepository, AccountTransactionRepository accountTransactionRepository) {
        this.accountRepository = accountRepository;
        this.accountTransactionRepository = accountTransactionRepository;
    }

    @Override
    public Account get(UUID id) {
        return accountRepository.get(id).orElseThrow(() -> new Err(Status.NOT_FOUND, "Account not found"));
    }

    @Override
    public List<Account> getAll(UUID userId) {
        return accountRepository.getAll(userId);
    }

    @Override
    public Account create(AccountRequest request) {
        request.validate();
        Account account = Account.builder().id(UUID.randomUUID()).userId(request.getUserId())
                .currency(request.getCurrency()).createDate(Instant.now())
                .build();
        accountRepository.create(account);
        return account;
    }

}
