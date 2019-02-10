package com.eren.revolut.repository;

import com.eren.revolut.model.entity.AccountTransaction;
import com.eren.revolut.model.entity.Transaction;
import org.jooby.mvc.Body;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Account Transaction Repository
 *
 * @see <a href="https://www.martinfowler.com/eaaDev/AccountingTransaction.html">Accounting Transaction article by Martin Fowler</a>
 */
public interface AccountTransactionRepository {

    /**
     * Create double entry transactions one for sender and one for receiver account
     *
     * @param transaction the transaction
     */
    void createTransfer(Transaction transaction);

    /**
     * Create entry transactions one for sender and one for receiver account
     *
     * @param transaction the transaction
     */
    void createWithdraw(Transaction transaction);

    /**
     * Create receiver.
     *
     * @param transaction the transaction
     */
    void createDeposit(Transaction transaction);

    /**
     * Gets sum of all transaction amounts for the account
     *
     * @param account the account
     * @return the balance
     */
    BigDecimal getBalance(UUID account);

    /**
     * Gets account transactions by the account.
     *
     * @param account the account
     * @return the account transactions
     */
    List<AccountTransaction> getAccountTransactions(@Body UUID account);

}
