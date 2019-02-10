package com.eren.revolut.web;

import com.eren.revolut.Application;
import com.eren.revolut.model.Currency;
import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.model.web.DepositRequest;
import com.eren.revolut.model.web.TransferRequest;
import com.eren.revolut.model.web.WithdrawRequest;
import org.jooby.test.JoobyRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionCouncurrencyTest extends BaseApiTest {

    private int dataCount = 100;
    private int treadCount = 100;

    static Application application = new Application();

    @ClassRule
    public static JoobyRule bootstrap = new JoobyRule(application);

    @Override
    public Application getApplication() {
        return application;
    }

    @Test
    public void transfer_givenOneSenderAndMultipleReceiverAccounts_thenSuccess() throws InterruptedException {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount1 = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount2 = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        deposit(new DepositRequest(senderAccount.getId(), BigDecimal.valueOf(900)));

        ExecutorService executor = Executors.newFixedThreadPool(treadCount);
        CountDownLatch latch = new CountDownLatch(dataCount * 2);
        for (int i = 0; i < dataCount; i++) {
            executeTransferThreadWithSuccess(executor, latch, new TransferRequest(senderAccount.getId(), receiverAccount1.getId(), BigDecimal.valueOf(5)));
            executeTransferThreadWithSuccess(executor, latch, new TransferRequest(senderAccount.getId(), receiverAccount2.getId(), BigDecimal.valueOf(5)));
        }
        latch.await();

        BigDecimal senderBalance = getBalance(senderAccount.getId());
        BigDecimal receiver1Balance = getBalance(receiverAccount1.getId());
        BigDecimal receiver2Balance = getBalance(receiverAccount2.getId());
        Assert.assertTrue(BigDecimal.valueOf(0).compareTo(senderBalance) == 0);
        Assert.assertTrue(BigDecimal.valueOf(900).compareTo(receiver1Balance.add(receiver2Balance)) == 0);
    }


    @Test
    public void transfer_givenMultipleTransferBetweenTwoAccounts_thenSuccess() throws InterruptedException {
        Account firstAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account secondAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        deposit(new DepositRequest(firstAccount.getId(), BigDecimal.valueOf(1000)));
        deposit(new DepositRequest(secondAccount.getId(), BigDecimal.valueOf(1000)));

        ExecutorService executor = Executors.newFixedThreadPool(treadCount);
        CountDownLatch latch = new CountDownLatch(dataCount * 2);
        for (int i = 0; i < dataCount; i++) {
            executeTransferThreadWithSuccess(executor, latch, new TransferRequest(firstAccount.getId(), secondAccount.getId(), BigDecimal.valueOf(1.11)));
            executeTransferThreadWithSuccess(executor, latch, new TransferRequest(secondAccount.getId(), firstAccount.getId(), BigDecimal.valueOf(2.22)));
        }
        latch.await();

        BigDecimal firstBalance = getBalance(firstAccount.getId());
        BigDecimal secondBalance = getBalance(secondAccount.getId());
        Assert.assertTrue(BigDecimal.valueOf(1111).compareTo(firstBalance) == 0);
        Assert.assertTrue(BigDecimal.valueOf(889).compareTo(secondBalance) == 0);
    }

    @Test
    public void transfer_givenMultipleTransferBetweenThreeAccounts_thenSuccess() throws InterruptedException {
        Account firstAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.GBP));
        Account secondAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.GBP));
        Account thirdAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.GBP));
        deposit(new DepositRequest(firstAccount.getId(), BigDecimal.valueOf(1000)));
        deposit(new DepositRequest(secondAccount.getId(), BigDecimal.valueOf(1000)));
        deposit(new DepositRequest(thirdAccount.getId(), BigDecimal.valueOf(1000)));

        ExecutorService executor = Executors.newFixedThreadPool(treadCount);
        CountDownLatch latch = new CountDownLatch(dataCount * 3);
        for (int i = 0; i < dataCount; i++) {
            executeTransferThreadWithSuccess(executor, latch, new TransferRequest(firstAccount.getId(), secondAccount.getId(), BigDecimal.valueOf(1.11)));
            executeTransferThreadWithSuccess(executor, latch, new TransferRequest(secondAccount.getId(), thirdAccount.getId(), BigDecimal.valueOf(2.22)));
            executeTransferThreadWithSuccess(executor, latch, new TransferRequest(thirdAccount.getId(), firstAccount.getId(), BigDecimal.valueOf(5.55)));
        }
        latch.await();

        BigDecimal firstBalance = getBalance(firstAccount.getId());
        BigDecimal secondBalance = getBalance(secondAccount.getId());
        BigDecimal thirdBalance = getBalance(thirdAccount.getId());
        Assert.assertTrue(BigDecimal.valueOf(1444).compareTo(firstBalance) == 0);
        Assert.assertTrue(BigDecimal.valueOf(889).compareTo(secondBalance) == 0);
        Assert.assertTrue(BigDecimal.valueOf(667).compareTo(thirdBalance) == 0);
    }

    @Test
    public void transferAndWithdraw_givenOneSenderAccount_thenSuccess() throws InterruptedException {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        deposit(new DepositRequest(senderAccount.getId(), BigDecimal.valueOf(900)));

        ExecutorService executor = Executors.newFixedThreadPool(treadCount);
        CountDownLatch latch = new CountDownLatch(dataCount * 2);
        for (int i = 0; i < dataCount; i++) {
            executeTransferThreadWithSuccess(executor, latch, new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(5)));
            executeWithrawThreadWithSuccess(executor, latch, new WithdrawRequest(senderAccount.getId(), BigDecimal.valueOf(5)));
        }
        latch.await();

        BigDecimal senderBalance = getBalance(senderAccount.getId());
        BigDecimal receiverBalance = getBalance(receiverAccount.getId());
        Assert.assertTrue(BigDecimal.valueOf(0).compareTo(senderBalance) == 0);
        Assert.assertTrue(receiverBalance.compareTo(BigDecimal.ZERO) > 0);
    }

    public void executeTransferThreadWithSuccess(ExecutorService executor, CountDownLatch latch, TransferRequest request) {
        executor.execute(new Thread(() -> {
            try {
                executeTransferWithSuccess(request);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    public void executeWithrawThreadWithSuccess(ExecutorService executor, CountDownLatch latch, WithdrawRequest request) {
        executor.execute(new Thread(() -> {
            try {
                withdraw(request);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
