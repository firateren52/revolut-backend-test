package com.eren.revolut.web;

import com.eren.revolut.Application;
import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.model.web.*;
import com.eren.revolut.service.AccountService;
import com.eren.revolut.service.TransactionService;
import io.restassured.mapper.ObjectMapperType;
import org.jooby.Status;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public abstract class BaseApiTest {
    public static String TRANSACTION_API_PATH = "/transactions";
    static String CONTENT_TYPE_JSON = "application/json";

    public abstract Application getApplication();

    public Account createAccount(AccountRequest request) {
        return getApplication().require(AccountService.class).create(request);
    }

    public Transaction deposit(DepositRequest request) {
        return getApplication().require(TransactionService.class).deposit(request);
    }

    public Transaction withdraw(WithdrawRequest request) {
        return getApplication().require(TransactionService.class).withdraw(request);
    }

    public Transaction transfer(TransferRequest request) {
        return getApplication().require(TransactionService.class).transfer(request);
    }

    public BigDecimal getBalance(UUID account) {
        return getApplication().require(TransactionService.class).getBalance(account);
    }

    public void executeTransferWithBadRequest(TransferRequest request, String errorMessage) {
        given().when()
                .body(request)
                .contentType(CONTENT_TYPE_JSON)
                .post(TRANSACTION_API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString(errorMessage));
    }

    public TransactionResponse executeTransferWithSuccess(TransferRequest request) {
        return given().when()
                .body(request)
                .contentType(CONTENT_TYPE_JSON)
                .post(TRANSACTION_API_PATH)
                .then()
                .statusCode(Status.OK.value())
                .extract()
                .response()
                .as(TransactionResponse.class, ObjectMapperType.JACKSON_2);
    }
}
