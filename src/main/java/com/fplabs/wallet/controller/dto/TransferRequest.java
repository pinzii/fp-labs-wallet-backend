package com.fplabs.wallet.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private String idempotencyKey;
}