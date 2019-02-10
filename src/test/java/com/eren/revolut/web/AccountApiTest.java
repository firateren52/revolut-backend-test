package com.eren.revolut.web;

import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.Currency;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.model.web.AccountResponse;
import io.restassured.mapper.ObjectMapperType;
import org.jooby.Status;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;

public class AccountApiTest extends BaseApiTest {
    private static String ACCOUNT_API_PATH = "/accounts";

    @Test
    public void get_givenExistentId_thenGetAccount() {
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
    public void get_givenNonexistentId_thenThrowNotFoundError() {
        UUID id = UUID.randomUUID();

        given().when()
                .contentType(CONTENT_TYPE_JSON)
                .get(ACCOUNT_API_PATH + "/" + id)
                .then()
                .statusCode(Status.NOT_FOUND.value())
                .body(containsString("Account not found"));
    }

    @Test
    public void getAll_givenExistentId_thenGetAccount() {
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
    public void getAll_givenNonexistentId_thenThrowServerError() {
        given().when()
                .contentType(CONTENT_TYPE_JSON)
                .get(ACCOUNT_API_PATH + "?userId=" + null)
                .then()
                .statusCode(Status.SERVER_ERROR.value())
                .body(containsString("Invalid UUID string: null"));

    }

    @Test
    public void create_givenValidUseridAndCurrency_thenCreateNewAccount() {
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
    public void create_givenInvalidUseridAndCurrency_thenThrowBadRequestError() {
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

}