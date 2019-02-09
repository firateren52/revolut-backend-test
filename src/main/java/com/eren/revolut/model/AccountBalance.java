package com.eren.revolut.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AccountBalance {
    private final UUID id;
    private final Account account;
    private final BigDecimal transferAmount;
}
