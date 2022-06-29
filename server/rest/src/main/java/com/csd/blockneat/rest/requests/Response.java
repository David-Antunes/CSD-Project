package com.csd.blockneat.rest.requests;

import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.entities.Signed;

public record Response(Signed<Either<Integer>> response) {
}
