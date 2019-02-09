package com.eren.revolut.repository.impl;

import com.eren.revolut.model.Account;
import com.eren.revolut.repository.AccountRepository;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class AccountMemoryRepository implements AccountRepository {

    private final List<Account> accountTable = new ArrayList<>();

    @Override
    public Optional<Account> getAccount(UUID id) {
        return accountTable.stream().filter(account -> account.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<Account> getAccount(UUID userId, Currency currency) {
        return null;
    }

    @Override
    public List<Account> getAccountsByUser(UUID userId) {
        return accountTable.stream().filter(account -> account.getUserId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public void createAccount(Account account) {
        //TODO(firat.eren) check for duplicate ids
        accountTable.add(account);
    }

}
