package com.eren.revolut.repository.impl;

import com.eren.revolut.model.entity.Account;
import com.eren.revolut.repository.AccountRepository;

import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class AccountMemoryRepository implements AccountRepository {

    private final Set<Account> table = new HashSet<>();

    @Override
    public Optional<Account> get(UUID id) {
        return table.stream()
                .filter(account -> account.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Account> get(UUID userId, Currency currency) {
        return null;
    }

    @Override
    public List<Account> getAll(UUID userId) {
        return table.stream()
                .filter(account -> account.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void create(Account account) {
        //TODO(firat.eren) check for duplicate ids
        table.add(account);
    }

}
