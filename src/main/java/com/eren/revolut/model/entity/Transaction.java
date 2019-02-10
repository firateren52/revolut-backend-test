package com.eren.revolut.model.entity;

import com.eren.revolut.model.TransactionStatus;
import com.eren.revolut.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Transaction {

    private final UUID id;
    private final Account senderAccount;
    private final Account receiverAccount;
    private final BigDecimal amount;
    private TransactionStatus status;
    private final Instant createDate;
    private Instant updateDate;
    private final TransactionType transactionType;

    public Transaction setStatus(TransactionStatus status) {
        this.status = status;
        this.updateDate = Instant.now();
        return this;
    }
}

