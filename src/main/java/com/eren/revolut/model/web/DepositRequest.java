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
public class DepositRequest {

    private UUID account;
    private BigDecimal amount;

    public void validate() {
        if(account == null) {
            throw new Err(Status.BAD_REQUEST, "account cannot be null");
        }
        if(amount.signum() <= 0) {
            throw new Err(Status.BAD_REQUEST, "amount must be greater than zero");
        }
    }
}
