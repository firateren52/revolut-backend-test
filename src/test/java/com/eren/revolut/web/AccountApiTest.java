package com.eren.revolut.web;

import com.eren.revolut.Application;
import com.eren.revolut.model.Account;
import com.eren.revolut.model.Currency;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.model.web.AccountResponse;
import com.eren.revolut.service.AccountService;
import io.restassured.mapper.ObjectMapperType;
import org.jooby.Status;
import org.jooby.test.JoobyRule;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

public class AccountApiTest {
    private static String CONTENT_TYPE_JSON = "application/json";
    private static String ACCOUNT_API_PATH = "/accounts";

    private static Application application = new Application();

    @ClassRule
    public static JoobyRule bootstrap = new JoobyRule(application);

    @Test
    public void getAccount_givenExistentId_thenGetAccount() {
        UUID userId = UUID.randomUUID();
        Account actualAccount = createAccount(new AccountRequest(userId, Currency.GBP));

        Account account = given().when()
                .contentType(CONTENT_TYPE_JSON)
                .get(ACCOUNT_API_PATH + "/" + actualAccount.getId())
                .then()
                .statusCode(Status.OK.value())
                .extract()
                .response()
                .as(Account.class, ObjectMapperType.JACKSON_2);
        Assert.assertEquals(account, actualAccount);

    }

    @Test
    public void getAccount_givenNonexistentId_thenGetNotFoundError() {
        UUID id = UUID.randomUUID();

        given().when()
                .contentType(CONTENT_TYPE_JSON)
                .get(ACCOUNT_API_PATH + "/" + id)
                .then()
                .statusCode(Status.NOT_FOUND.value())
                .body(containsString("Account not found"));
    }

    @Test
    public void getAccountsByUser_givenExistentId_thenGetAccount2() {
        UUID userId = UUID.randomUUID();
        Account actualGBPAccount = createAccount(new AccountRequest(userId, Currency.GBP));
        Account actualEURAccount = createAccount(new AccountRequest(userId, Currency.EUR));
        Account actualUSDAccount = createAccount(new AccountRequest(userId, Currency.USD));
        List<Account> actualAccounts =  Arrays.asList(actualEURAccount,actualGBPAccount,actualUSDAccount);

        Account[] accounts = given().when()
                .contentType(CONTENT_TYPE_JSON)
                .get(ACCOUNT_API_PATH + "?userId=" + userId)
                .then()
                .statusCode(Status.OK.value())
                .extract()
                .response()
                .as(Account[].class, ObjectMapperType.JACKSON_2);
        assertThat(actualAccounts, containsInAnyOrder(accounts));
    }

    @Test
    public void getAccountsByUser_givenNonexistentId_thenThrowError() {
        given().when()
                .contentType(CONTENT_TYPE_JSON)
                .get(ACCOUNT_API_PATH + "?userId=" + null)
                .then()
                .statusCode(Status.SERVER_ERROR.value())
                .body(containsString("Invalid UUID string: null"));

    }

    @Test
    public void createAccount_givenValidUseridAndCurrency_thenCreateNewAccount() {
        UUID userId = UUID.randomUUID();
        AccountResponse accountResponse = given().when()
                .body(new AccountRequest(userId, Currency.EUR))
                .contentType(CONTENT_TYPE_JSON)
                .post(ACCOUNT_API_PATH)
                .then()
                .statusCode(Status.OK.value())
                .extract()
                .response()
                .as(AccountResponse.class, ObjectMapperType.JACKSON_2);
        Assert.assertNotNull(getAccount(accountResponse.getId()));
    }

    @Test
    public void createAccount_givenInvalidUseridAndCurrency_thenThrowError() {
        given().when()
                .body(new AccountRequest(null, Currency.EUR))
                .contentType(CONTENT_TYPE_JSON)
                .post(ACCOUNT_API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString("userId cannot be null"));

        UUID userId = UUID.randomUUID();
        given().when()
                .body(new AccountRequest(userId, null))
                .contentType(CONTENT_TYPE_JSON)
                .post(ACCOUNT_API_PATH)
                .then()
                .statusCode(Status.BAD_REQUEST.value())
                .body(containsString("currency cannot be null"));
    }

    private Account getAccount(UUID id) {
        return application.require(AccountService.class).getAccount(id);
    }

    private Account createAccount(AccountRequest request) {
        return application.require(AccountService.class).createAccount(request);
    }

}