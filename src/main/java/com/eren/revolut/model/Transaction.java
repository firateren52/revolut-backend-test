package com.eren.revolut.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Transaction {
    private final UUID id;
    private final Account senderAccount;
    private final Account receiverAccount;
    private TransactionStatus status;
    private final long createDate;
}

enum TransactionStatus {
    PENDING, COMPLETED, FAILED, REVERTED, DECLINED;
}