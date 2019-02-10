package com.eren.revolut.web;

import com.eren.revolut.Application;
import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.Currency;
import com.eren.revolut.model.TransactionStatus;
import com.eren.revolut.model.entity.AccountTransaction;
import com.eren.revolut.model.web.*;
import io.restassured.mapper.ObjectMapperType;
import org.jooby.Status;
import org.jooby.test.JoobyRule;
import org.junit.ClassRule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TransactionApiTest extends BaseApiTest {
    public static String API_PATH = "/transactions";

    static Application application = new Application();

    @ClassRule
    public static JoobyRule bootstrap = new JoobyRule(application);

    @Override
    public Application getApplication() {
        return application;
    }

    @Test
    public void transfer_givenInvalidAccounts_thenThrowBadRequestError() {
        Account account = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));

        executeTransferWithBadRequest(new TransferRequest(null, account.getId(), BigDecimal.valueOf(10)), "senderAccount cannot be null");
        executeTransferWithBadRequest(new TransferRequest(account.getId(), null, BigDecimal.valueOf(10)), "receiverAccount cannot be null");
        executeTransferWithBadRequest(new TransferRequest(account.getId(), account.getId(), BigDecimal.valueOf(10)), "accounts cannot be same");
    }

    @Test
    public void transfer_givenInvalidAmounts_thenThrowBadRequestError() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));

        executeTransferWithBadRequest(new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(-10)), "amount must be greater than zero");
        executeTransferWithBadRequest(new TransferRequest(senderAccount.getId(), receiverAccount.getId(), null), "amount cannot be null");
    }

    @Test
    public void transfer_givenDifferentCurrencyAccounts_thenDeclined() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.EUR));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));

        executeTransferWithBadRequest(new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(10)), "accounts have different currencies");
    }

    @Test
    public void transfer_givenInsufficientAmount_thenDeclined() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));

        TransactionResponse response = executeTransferWithSuccess(new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(10)));

        assertThat(TransactionStatus.DECLINED, equalTo(response.getStatus()));
    }

    @Test
    public void transfer_givenSufficientAmounts_thenCompleted() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        deposit(new DepositRequest(senderAccount.getId(), BigDecimal.valueOf(100)));

        TransactionResponse response = executeTransferWithSuccess(new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(30)));

        assertThat(TransactionStatus.COMPLETED, equalTo(response.getStatus()));
        assertThat(BigDecimal.valueOf(70), equalTo(getBalance(senderAccount.getId())));
        assertThat(BigDecimal.valueOf(30), equalTo(getBalance(receiverAccount.getId())));
    }

    @Test
    public void getAccountTransactions_givenDifferentTransactions_thenSuccess() {
        Account senderAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        getBalance(senderAccount.getId());
        deposit(new DepositRequest(senderAccount.getId(), BigDecimal.valueOf(1000)));
        withdraw(new WithdrawRequest(senderAccount.getId(), BigDecimal.valueOf(100)));

        Account receiverAccount = createAccount(new AccountRequest(UUID.randomUUID(), Currency.USD));
        deposit(new DepositRequest(receiverAccount.getId(), BigDecimal.valueOf(200)));
        transfer(new TransferRequest(senderAccount.getId(), receiverAccount.getId(), BigDecimal.valueOf(400)));

        // check sender account transactions
        AccountTransaction[] senderAccountTransactions = given().when()
                .contentType(CONTENT_TYPE_JSON)
                .get(API_PATH + "/accounts/" + senderAccount.getId())
                .then()
                .statusCode(Status.OK.value())
                .extract()
                .response()
                .as(AccountTransaction[].class, ObjectMapperType.JACKSON_2);
        assertThat(senderAccountTransactions, arrayWithSize(3));

        // check receiver account transactions
        AccountTransaction[] receiverAccountTransactions = given().when()
                .contentType(CONTENT_TYPE_JSON)
                .get(API_PATH + "/accounts/" + receiverAccount.getId())
                .then()
                .statusCode(Status.OK.value())
                .extract()
                .response()
                .as(AccountTransaction[].class, ObjectMapperType.JACKSON_2);
        assertThat(receiverAccountTransactions, arrayWithSize(2));

        // check sender and receiver balances
        assertThat(BigDecimal.valueOf(500), equalTo(getBalance(senderAccount.getId())));
        assertThat(BigDecimal.valueOf(600), equalTo(getBalance(receiverAccount.getId())));
    }
}
