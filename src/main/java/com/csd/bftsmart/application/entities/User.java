package com.csd.bftsmart.application.entities;

import java.io.Serializable;
import java.util.ArrayList;

public record User(String id, ArrayList<Account> accounts) implements Serializable {
}
