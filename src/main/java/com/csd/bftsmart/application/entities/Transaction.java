package com.csd.bftsmart.application.entities;

import java.io.Serializable;

public record Transaction(int id, Account from, Account to, int value) implements Serializable {
}
