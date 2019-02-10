package com.eren.revolut.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class AccountTransaction {

    private final UUID id;
    private final Account account;
    private final Transaction transaction;
    private final BigDecimal amount;
}
