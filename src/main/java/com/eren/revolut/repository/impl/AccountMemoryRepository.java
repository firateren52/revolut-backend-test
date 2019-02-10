package com.eren.revolut.repository.impl;

import com.eren.revolut.model.entity.Account;
import com.eren.revolut.repository.AccountRepository;

import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class AccountMemoryRepository implements AccountRepository {

    private final Map<UUID, Account> table = new ConcurrentHashMap<>();

    @Override
    public Optional<Account> get(UUID id) {
        return table.values().stream()
                .filter(account -> account.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Account> getAll(UUID userId) {
        return table.values().stream()
                .filter(account -> account.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void create(Account account) {
        table.put(account.getId(), account);
    }

}
