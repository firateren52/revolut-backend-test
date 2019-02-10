package com.eren.revolut.repository.impl;


import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.entity.AccountTransaction;
import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.repository.AccountTransactionRepository;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.*;

@Singleton
public class AccountTransactionMemoryRepository implements AccountTransactionRepository {

    private final Set<AccountTransaction> table = new HashSet<>();

    @Override
    public void createTransfer(Transaction transaction) {
        createWithdraw(transaction);
        createDeposit(transaction);
    }

    @Override
    public void createWithdraw(Transaction transaction) {
        AccountTransaction senderTransaction = AccountTransaction.builder().id(UUID.randomUUID())
                .account(transaction.getSenderAccount()).transaction(transaction)
                .amount(transaction.getAmount().negate()).build();
        table.add(senderTransaction);
    }

    @Override
    public void createDeposit(Transaction transaction) {
        AccountTransaction receiverTransaction = AccountTransaction.builder().id(UUID.randomUUID())
                .account(transaction.getReceiverAccount()).transaction(transaction)
                .amount(transaction.getAmount()).build();
        table.add(receiverTransaction);
    }

    @Override
    public BigDecimal getBalance(Account account) {
        return table.stream().filter(row -> row.getAccount().equals(account))
                .map(AccountTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
