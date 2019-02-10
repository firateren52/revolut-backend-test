package com.eren.revolut.repository.impl;

import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.repository.TransactionRepository;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class TransactionMemoryRepository implements TransactionRepository {

    private final Set<Transaction> table = new HashSet<>();

    @Override
    public void create(Transaction transaction) {
        table.add(transaction);
    }
}
