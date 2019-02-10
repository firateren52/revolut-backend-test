package com.eren.revolut.repository.impl;


import com.eren.revolut.model.entity.AccountTransaction;
import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.repository.AccountTransactionRepository;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Singleton
public class AccountTransactionMemoryRepository implements AccountTransactionRepository {

    private final Map<UUID, AccountTransaction> table = new ConcurrentHashMap<>();

    @Override
    public void createTransfer(Transaction transaction) {
        createWithdraw(transaction);
        createDeposit(transaction);
    }

    @Override
    public void createWithdraw(Transaction transaction) {
        AccountTransaction accountTransaction = AccountTransaction.builder().id(UUID.randomUUID())
                .account(transaction.getSenderAccount().getId()).transaction(transaction.getId())
                .amount(transaction.getAmount().negate()).createDate(transaction.getCreateDate()).build();
        table.put(accountTransaction.getId(), accountTransaction);
    }

    @Override
    public void createDeposit(Transaction transaction) {
        AccountTransaction accountTransaction = AccountTransaction.builder().id(UUID.randomUUID())
                .account(transaction.getReceiverAccount().getId()).transaction(transaction.getId())
                .amount(transaction.getAmount()).createDate(transaction.getCreateDate()).build();
        table.put(accountTransaction.getId(), accountTransaction);
    }

    @Override
    public BigDecimal getBalance(UUID account) {
        return table.values().stream().filter(row -> row.getAccount().equals(account))
                .map(AccountTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<AccountTransaction> getAccountTransactions(UUID account) {
        return table.values().stream().filter(row -> row.getAccount().equals(account))
                .sorted(Comparator.comparing(AccountTransaction::getCreateDate))
                .collect(Collectors.toList());
    }

}
