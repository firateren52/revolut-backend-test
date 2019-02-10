package com.eren.revolut.web;

import com.eren.revolut.Application;
import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.model.web.DepositRequest;
import com.eren.revolut.repository.impl.AccountTransactionMemoryRepository;
import com.eren.revolut.service.AccountService;
import com.eren.revolut.service.TransactionService;
import org.jooby.test.JoobyRule;
import org.junit.ClassRule;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class BaseApiTest {
    static String CONTENT_TYPE_JSON = "application/json";
    static Application application = new Application();

    @ClassRule
    public static JoobyRule bootstrap = new JoobyRule(application);

    public Account getAccount(UUID id) {
        return application.require(AccountService.class).get(id);
    }

    public Account createAccount(AccountRequest request) {
        return application.require(AccountService.class).create(request);
    }

    public Transaction deposit(DepositRequest request) {
        return application.require(TransactionService.class).deposit(request);
    }

    public BigDecimal getBalance(Account account) {
        return application.require(AccountTransactionMemoryRepository.class).getBalance(account);
    }
}
