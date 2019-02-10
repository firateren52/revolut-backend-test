package com.eren.revolut.repository;

import com.eren.revolut.model.entity.Account;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The interface Account repository.
 */
public interface AccountRepository {

    /**
     * Get optional account by id.
     *
     * @param id the id
     * @return the optional
     */
    Optional<Account> get(UUID id);

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
     * @param account the account
     */
    void create(Account account);

}
