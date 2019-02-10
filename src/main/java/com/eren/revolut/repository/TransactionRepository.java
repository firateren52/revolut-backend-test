package com.eren.revolut.repository;

import com.eren.revolut.model.entity.Transaction;

public interface TransactionRepository {

    void create(Transaction transaction);
}
