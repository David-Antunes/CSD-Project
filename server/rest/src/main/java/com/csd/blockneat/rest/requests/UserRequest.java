package com.csd.blockneat.rest.requests;

import com.csd.blockneat.application.entities.User;

public record UserRequest(User.Id userId) {}
