package com.eren.revolut.service.impl;

import com.eren.revolut.model.TransactionStatus;
import com.eren.revolut.model.TransactionType;
import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.entity.AccountTransaction;
import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.model.web.DepositRequest;
import com.eren.revolut.model.web.TransferRequest;
import com.eren.revolut.model.web.WithdrawRequest;
import com.eren.revolut.repository.AccountTransactionRepository;
import com.eren.revolut.repository.TransactionRepository;
import com.eren.revolut.service.AccountService;
import com.eren.revolut.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.jooby.Err;
import org.jooby.Status;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
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

        // We need a lock on sender account instance to compare sender balance and transfer amount. We assume there will be only one instance for an account
        // We don't need a lock on the receiver account since we don't persist account balance (the account balance get computed every time we access it)
        synchronized (senderAccount) {
            log.debug("started transfer lock for account: " + senderAccount.getId());

            Transaction transaction = Transaction.builder().id(UUID.randomUUID())
                    .senderAccount(senderAccount).receiverAccount(receiverAccount).amount(request.getAmount())
                    .transactionType(TransactionType.TRANSFER).status(TransactionStatus.PENDING)
                    .createDate(Instant.now()).updateDate(Instant.now()).build();

            try {
                transactionRepository.create(transaction);
                BigDecimal senderBalance = getBalance(senderAccount.getId());
                if (senderBalance.compareTo(request.getAmount()) < 0) {
                    log.warn("insufficient amount: + ", request.toString());
                    transaction.setStatus(TransactionStatus.DECLINED);
                } else {
                    accountTransactionRepository.createTransfer(transaction);
                    transaction.setStatus(TransactionStatus.COMPLETED);
                }
            } catch (Exception ex) {
                log.error("transfer failed request: + ", senderAccount.getId().toString());
                transaction.setStatus(TransactionStatus.FAILED);
            }

            log.debug("finished transfer lock for account: " + senderAccount.getId());
            return transaction;
        }
    }

    @Override
    public Transaction withdraw(WithdrawRequest request) {
        Account account = accountService.get(request.getAccount());

        // We need a lock on account instance to compare account balance and transfer amount
        synchronized (account) {
            log.debug("started withdraw lock for account: " + account.getId());
            Transaction transaction = Transaction.builder().id(UUID.randomUUID()).amount(request.getAmount())
                    .senderAccount(account).receiverAccount(null).transactionType(TransactionType.WITHDRAW)
                    .status(TransactionStatus.PENDING).createDate(Instant.now()).updateDate(Instant.now()).build();
            try {
                transactionRepository.create(transaction);
                BigDecimal accountBalance = getBalance(account.getId());
                if (accountBalance.compareTo(request.getAmount()) < 0) {
                    transaction.setStatus(TransactionStatus.DECLINED);
                } else {
                    accountTransactionRepository.createWithdraw(transaction);
                    transaction.setStatus(TransactionStatus.COMPLETED);
                }
            } catch (Exception ex) {
                transaction.setStatus(TransactionStatus.FAILED);
            }
            log.debug("finished withdraw lock for account: " + account.getId());
            return transaction;
        }
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

    @Override
    public BigDecimal getBalance(UUID account) {
        BigDecimal balance = accountTransactionRepository.getBalance(account);
        if (balance.signum() < 0) {
            log.error("balance is negative for account: ", account.toString());
            throw new Err(Status.BAD_REQUEST, "account balance is negative!");
        }
        return balance;
    }

    @Override
    public List<AccountTransaction> getAccountTransactions(UUID account) {
        if (account == null) {
            throw new Err(Status.BAD_REQUEST, "account cannot be null");
        }
        return accountTransactionRepository.getAccountTransactions(account);
    }

}
