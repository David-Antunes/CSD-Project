package com.csd.bftsmart.application.entities;

import java.io.Serializable;
import java.util.ArrayList;

public record User(Id id, ArrayList<Account> accounts) implements Serializable {
    public record Id(String email, String base64pk) implements Comparable<User.Id>, Serializable {
        @Override
        public int compareTo(Id id) {
            int email = this.email.compareTo(id.email);
            if (email != 0)
                return email;
            return this.base64pk.compareTo(id.base64pk);
        }
    }
}
