package com.eren.revolut.service;

import com.eren.revolut.model.entity.Account;
import com.eren.revolut.model.web.AccountRequest;

import java.util.List;
import java.util.UUID;

/**
 * The interface Account service.
 */
public interface AccountService {

    /**
     * Get account by id.
     *
     * @param id the id
     * @return the account
     */
    Account get(UUID id);

    /**
     * Gets all accounts by user.
     *
     * @param userId the user id
     * @return the all
     */
    List<Account> getAll(UUID userId);

    /**
     * Create new account.
     *
     * @param request the request
     * @return the account
     */
    Account create(AccountRequest request);
}
