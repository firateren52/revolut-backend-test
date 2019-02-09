package com.eren.revolut.service;

import com.eren.revolut.model.Account;
import com.eren.revolut.model.Currency;
import com.eren.revolut.model.web.AccountRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    Account getAccount(UUID id);

    List<Account> getAccountsByUser(UUID userId);

    Account createAccount(AccountRequest accountRequest);
}
