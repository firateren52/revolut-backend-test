package com.eren.revolut.web;


import com.eren.revolut.model.Account;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.model.web.AccountResponse;
import com.eren.revolut.service.AccountService;
import org.jooby.Err;
import org.jooby.Status;
import org.jooby.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/accounts")
public class AccountApi {

    private final AccountService accountService;

    @Inject
    public AccountApi(AccountService accountService) {
        this.accountService = accountService;
    }

    @Path("/{id}")
    @GET
    public Account getAccount(UUID id) {
        return accountService.getAccount(id);
    }

    @GET
    public List<Account> getAccountsByUser(@Named("userId") UUID userId) {
        return accountService.getAccountsByUser(userId);
    }

    @POST
    public AccountResponse createAccount(@Body AccountRequest accountRequest) {
        Account account =  accountService.createAccount(accountRequest);
        return new AccountResponse(account.getId(), account.getCreateDate());
    }
}

