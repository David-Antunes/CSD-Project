package com.csd.bftsmart.rest.requests;

import com.csd.bftsmart.application.entities.User;

public record AccountRequest(User.Id userId, String accountId) {
}
