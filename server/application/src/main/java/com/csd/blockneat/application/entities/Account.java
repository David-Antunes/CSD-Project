package com.csd.blockneat.application.entities;

import java.io.Serializable;

public record Account(String id, User.Id userId) implements Serializable,Comparable<Account> {
    

    public boolean equals(Object account) {
        if (account == null)
            return false;
        if(!(account instanceof Account))
            return false;

        return id.equals(((Account) account).id());
    }

    @Override
    public int compareTo(Account account) {
        return id.compareTo(account.id());
    }
}
