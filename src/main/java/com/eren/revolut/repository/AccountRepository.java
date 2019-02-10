package com.eren.revolut.repository;

import com.eren.revolut.model.entity.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Optional<Account> get(UUID id);

    List<Account> getAll(UUID userId);

    void create(Account account);

}
