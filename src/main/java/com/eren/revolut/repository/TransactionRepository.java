package com.eren.revolut.repository;

import com.eren.revolut.model.entity.Transaction;

/**
 * The interface Transaction repository.
 */
public interface TransactionRepository {

    /**
     * Persist new transaction.
     *
     * @param transaction the transaction
     */
    void create(Transaction transaction);
}
