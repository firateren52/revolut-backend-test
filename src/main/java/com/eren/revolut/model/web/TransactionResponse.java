package com.eren.revolut.model.web;

import com.eren.revolut.model.entity.Transaction;
import com.eren.revolut.model.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private UUID id;
    private TransactionStatus status;
    private Instant createDate;
    private Instant updateDate;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.status = transaction.getStatus();
        this.createDate = transaction.getCreateDate();
        this.updateDate = transaction.getUpdateDate();
    }
}
