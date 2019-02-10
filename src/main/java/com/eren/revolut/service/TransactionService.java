package com.eren.revolut.service;

import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.model.web.DepositRequest;
import com.eren.revolut.model.web.TransferRequest;
import com.eren.revolut.model.web.WithdrawRequest;

public interface TransactionService {

    /**
     * Create transaction and account transactions for both sender and receivers
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
}
