package com.eren.revolut.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Account {
    private UUID id;
    private UUID userId;
    private BigDecimal balance;
    private Currency currency;
    private Instant createDate;
    //TODO(firat.eren) add accountStatus
}