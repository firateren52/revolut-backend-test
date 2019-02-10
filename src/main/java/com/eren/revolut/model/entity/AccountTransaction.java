package com.eren.revolut.model.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class AccountTransaction {

    private UUID id;
    private UUID account;
    private UUID transaction;
    private BigDecimal amount;
    private Instant createDate;
}
