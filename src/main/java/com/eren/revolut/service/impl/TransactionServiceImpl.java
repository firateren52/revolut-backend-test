package com.eren.revolut.service.impl;

import com.eren.revolut.model.TransactionStatus;
import com.eren.revolut.model.TransactionType;
import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.model.web.DepositRequest;
import com.eren.revolut.model.web.TransferRequest;
import com.eren.revolut.model.web.WithdrawRequest;
import com.eren.revolut.repository.AccountTransactionRepository;
import com.eren.revolut.repository.TransactionRepository;
import com.eren.revolut.service.AccountService;
import com.eren.revolut.service.TransactionService;
import org.jooby.Err;
import org.jooby.Status;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Singleton
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountTransactionRepository accountTransactionRepository;

    private final AccountService accountService;

    @Inject
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountTransactionRepository accountTransactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountTransactionRepository = accountTransactionRepository;
        this.accountService = accountService;
    }

    @Override
    public Transaction transfer(TransferRequest request) {
        request.validate();

        Account senderAccount = accountService.get(request.getSenderAccount());
        Account receiverAccount = accountService.get(request.getReceiverAccount());

        if (!senderAccount.getCurrency().equals(receiverAccount.getCurrency())) {
            throw new Err(Status.BAD_REQUEST, "accounts have different currencies");
        }

        Transaction transaction = Transaction.builder().id(UUID.randomUUID())
                .senderAccount(senderAccount).receiverAccount(receiverAccount).amount(request.getAmount())
                .transactionType(TransactionType.TRANSFER).status(TransactionStatus.PENDING)
                .createDate(Instant.now()).updateDate(Instant.now()).build();

        // We need a lock on the sender account instance to compare sender balance and transfer amount
        // We don't need a lock on the receiver account since we don't persist account balance (the account balance get computed every time we access it)
        synchronized (senderAccount) {
            try {
                transactionRepository.create(transaction);
                BigDecimal senderBalance = accountTransactionRepository.getBalance(senderAccount);
                if (senderBalance.compareTo(request.getAmount()) < 0) {
                    transaction.setStatus(TransactionStatus.DECLINED);
                } else {
                    accountTransactionRepository.createTransfer(transaction);
                    transaction.setStatus(TransactionStatus.COMPLETED);
                }
            } catch (Exception ex) {
                transaction.setStatus(TransactionStatus.FAILED);
            }
        }
        return transaction;
    }

    @Override
    public Transaction withdraw(WithdrawRequest request) {
        Account account = accountService.get(request.getAccount());
        Transaction transaction = Transaction.builder().id(UUID.randomUUID()).amount(request.getAmount())
                .senderAccount(account).receiverAccount(null).transactionType(TransactionType.WITHDRAW)
                .status(TransactionStatus.PENDING).createDate(Instant.now()).updateDate(Instant.now()).build();

        synchronized (account) {
            try {
                transactionRepository.create(transaction);
                BigDecimal accountBalance = accountTransactionRepository.getBalance(account);
                if (accountBalance.compareTo(request.getAmount()) < 0) {
                    transaction.setStatus(TransactionStatus.DECLINED);
                } else {
                    accountTransactionRepository.createWithdraw(transaction);
                    transaction.setStatus(TransactionStatus.COMPLETED);
                }
            } catch (Exception ex) {
                transaction.setStatus(TransactionStatus.FAILED);
            }
        }
        return transaction;
    }

    @Override
    public Transaction deposit(DepositRequest request) {
        Account account = accountService.get(request.getAccount());
        Transaction transaction = Transaction.builder().id(UUID.randomUUID()).amount(request.getAmount())
                .senderAccount(null).receiverAccount(account).transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING).createDate(Instant.now()).updateDate(Instant.now()).build();

        try {
            transactionRepository.create(transaction);
            accountTransactionRepository.createDeposit(transaction);
            transaction.setStatus(TransactionStatus.COMPLETED);

        } catch (Exception ex) {
            transaction.setStatus(TransactionStatus.FAILED);
        }
        return transaction;
    }

}
