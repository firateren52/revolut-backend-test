package com.eren.revolut.model.entity;

import com.eren.revolut.model.Currency;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Account {

    private UUID id;
    private UUID userId;
    private Currency currency;
    private Instant createDate;
}