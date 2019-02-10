package com.eren.revolut.repository;

import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.entity.Transaction;

import java.math.BigDecimal;

/**
 * Account Transactio
 * @see <a href="https://www.martinfowler.com/eaaDev/AccountingTransaction.html">Accounting Transaction article by Martin Fowler</a>
 */
public interface AccountTransactionRepository {

    /**
     * Create double entry transactions one for sender and one for receiver account
     *
     * @param transaction the transaction
     *
     */
    void createTransfer(Transaction transaction);

    /**
     * reate entry transactions one for sender and one for receiver account
     *
     * @param transaction the transaction
     */
    void createWithdraw(Transaction transaction);

    /**
     * Create receiver.
     *
     * @param transaction the transaction
     */
    void createDeposit(Transaction transaction);

    /**
     * Sum of all transaction amounts for the given account
     *
     * @param account the account
     * @return the balance
     */
    BigDecimal getBalance(Account account);

}
