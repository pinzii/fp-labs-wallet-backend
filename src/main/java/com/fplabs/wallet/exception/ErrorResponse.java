package com.fplabs.wallet.exception;

public record ErrorResponse(
        String status,
        String message) {
}