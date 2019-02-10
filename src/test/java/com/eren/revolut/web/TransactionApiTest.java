package com.eren.revolut.web;

import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.Currency;
import com.eren.revolut.model.TransactionStatus;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.model.web.DepositRequest;
import com.eren.revolut.model.web.TransferRequest;
import com.eren.revolut.model.web.TransactionResponse;
import io.restassured.mapper.ObjectMapperType;
import org.jooby.Status;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class TransactionApiTest extends BaseApiTest {
    private static String API_PATH = "/transactions";

    @Test
    public void transfer_givenInvalidAccounts_thenThrowBadRequestError() {
        Account account = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));

        given().when()
                .body(new TransferRequest(null, account.getId(), BigDecimal.valueOf(10)))
                .contentType(CONTENT_TYPE_JSON)
                .post(API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString("senderAccount cannot be null"));

        given().when()
                .body(new TransferRequest(account.getId(), null, BigDecimal.valueOf(10)))
                .contentType(CONTENT_TYPE_JSON)
                .post(API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString("receiverAccount cannot be null"));


        given().when()
                .body(new TransferRequest(account.getId(), account.getId(), BigDecimal.valueOf(10)))
                .contentType(CONTENT_TYPE_JSON)
                .post(API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString("accounts cannot be same"));
    }

    @Test
    public void transfer_givenInvalidAmounts_thenThrowBadRequestError() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));

        given().when()
                .body(new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(-10)))
                .contentType(CONTENT_TYPE_JSON)
                .post(API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString("amount must be greater than zero"));

        given().when()
                .body(new TransferRequest(senderAccount.getId(), receiverAccount.getId(), null))
                .contentType(CONTENT_TYPE_JSON)
                .post(API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString("amount cannot be null"));
    }

    @Test
    public void transfer_givenDifferentCurrencyAccounts_thenDeclined() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.EUR));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));

        TransferRequest request = new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(10));
        given().when()
                .body(request)
                .contentType(CONTENT_TYPE_JSON)
                .post(API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString("accounts have different currencies"));

    }

    @Test
    public void transfer_givenInsufficientAmount_thenDeclined() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));

        TransferRequest request = new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(10));
        TransactionResponse response = given().when()
                .body(request)
                .contentType(CONTENT_TYPE_JSON)
                .post(API_PATH)
                .then()
                .statusCode(Status.OK.value())
                .extract()
                .response()
                .as(TransactionResponse.class, ObjectMapperType.JACKSON_2);

        Assert.assertEquals(TransactionStatus.DECLINED, response.getStatus());
    }

    @Test
    public void transfer_givenInsufficientAmount_thenCompleted() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        deposit(new DepositRequest(senderAccount.getId(), BigDecimal.valueOf(100)));

        TransferRequest request = new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(30));
        TransactionResponse response = given().when()
                .body(request)
                .contentType(CONTENT_TYPE_JSON)
                .post(API_PATH)
                .then()
                .statusCode(Status.OK.value())
                .extract()
                .response()
                .as(TransactionResponse.class, ObjectMapperType.JACKSON_2);

        Assert.assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        Assert.assertEquals(BigDecimal.valueOf(70), getBalance(senderAccount));
        Assert.assertEquals(BigDecimal.valueOf(30), getBalance(receiverAccount));
    }
}