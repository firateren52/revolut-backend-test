package com.eren.revolut.repository;

import com.eren.revolut.model.Account;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Optional<Account> getAccount(UUID id);

    Optional<Account> getAccount(UUID userId, Currency currency);

    List<Account> getAccountsByUser(UUID userId);

    void createAccount(Account account);

}
