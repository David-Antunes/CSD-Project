package com.csd.bftsmart.rest.requests;

import com.csd.bftsmart.application.entities.User;

public record UserRequest(User.Id userId) {}
