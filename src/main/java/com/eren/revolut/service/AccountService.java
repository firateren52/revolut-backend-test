package com.eren.revolut.service;

import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.web.AccountRequest;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    Account get(UUID id);

    List<Account> getAll(UUID userId);

    Account create(AccountRequest request);
}
