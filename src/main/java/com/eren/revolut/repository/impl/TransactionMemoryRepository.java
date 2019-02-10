package com.eren.revolut.repository.impl;

import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.repository.TransactionRepository;

import javax.inject.Singleton;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class TransactionMemoryRepository implements TransactionRepository {

    private final Map<UUID, Transaction> table = new ConcurrentHashMap<>();

    @Override
    public void create(Transaction transaction) {
        table.put(transaction.getId(), transaction);
    }
}
