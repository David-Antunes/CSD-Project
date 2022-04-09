package com.csd.bftsmart.application.repositories;

import com.csd.bftsmart.application.entities.Account;

public interface AccountRepository {
    Account save(Account account);

    void updateBalanceById(String id, int value);
}
