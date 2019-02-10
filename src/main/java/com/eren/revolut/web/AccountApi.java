package com.eren.revolut.web;


import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.web.AccountRequest;
import com.eren.revolut.model.web.AccountResponse;
import com.eren.revolut.service.AccountService;
import org.jooby.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
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
    public Account get(UUID id) {
        return accountService.get(id);
    }

    @GET
    public List<Account> getAll(@Named("userId") UUID userId) {
        return accountService.getAll(userId);
    }

    @POST
    public AccountResponse create(@Body AccountRequest request) {
        Account account =  accountService.create(request);
        return new AccountResponse(account.getId(), account.getCreateDate());
    }
}

