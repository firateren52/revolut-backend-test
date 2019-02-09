package com.eren.revolut.model.web;

import com.eren.revolut.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooby.Err;
import org.jooby.Status;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private UUID userId;
    private Currency currency;

    public void validate() {
        if(userId == null) {
            throw new Err(Status.BAD_REQUEST, "userId cannot be null");
        }
        if(currency == null) {
            throw new Err(Status.BAD_REQUEST, "currency cannot be null");
        }
    }
}
