package com.csd.bftsmart.rest.requests;

public record TransactionRequest(String from, String to, int value) {
}
