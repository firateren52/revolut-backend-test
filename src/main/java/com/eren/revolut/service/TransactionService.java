package com.eren.revolut.service;

import com.eren.revolut.model.entity.AccountTransaction;
import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.model.web.DepositRequest;
import com.eren.revolut.model.web.TransferRequest;
import com.eren.revolut.model.web.WithdrawRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * The interface Transaction service.
 */
public interface TransactionService {

    /**
     * Create transaction and account transactions for both sender and receiver
     *
     * @param request the request
     * @return the transaction
     */
    Transaction transfer(TransferRequest request);

    /**
     * Withdraw transaction.
     *
     * @param request the request
     * @return the transaction
     */
    Transaction withdraw(WithdrawRequest request);

    /**
     * Deposit transaction.
     *
     * @param request the request
     * @return the transaction
     */
    Transaction deposit(DepositRequest request);

    /**
     * Gets sum of all transaction amounts for the account
     *
     * @param account the account
     * @return the balance
     */
    BigDecimal getBalance(UUID account);

    /**
     * Gets account transactions.
     *
     * @param account the account
     * @return the account transactions
     */
    List<AccountTransaction> getAccountTransactions(UUID account);

}
