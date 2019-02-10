package com.eren.revolut.model.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooby.Err;
import org.jooby.Status;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    private UUID senderAccount;
    private UUID receiverAccount;
    private BigDecimal amount;

    public void validate() {
        //TODO(firat.eren) add error messages to config file
        if(senderAccount == null) {
            throw new Err(Status.BAD_REQUEST, "senderAccount cannot be null");
        }
        if(receiverAccount == null) {
            throw new Err(Status.BAD_REQUEST, "receiverAccount cannot be null");
        }
        if(senderAccount.equals(receiverAccount)) {
            throw new Err(Status.BAD_REQUEST, "accounts cannot be same");
        }
        if(amount == null) {
            throw new Err(Status.BAD_REQUEST, "amount cannot be null");
        }
        if(amount.signum() <= 0) {
            throw new Err(Status.BAD_REQUEST, "amount must be greater than zero");
        }
    }
}
