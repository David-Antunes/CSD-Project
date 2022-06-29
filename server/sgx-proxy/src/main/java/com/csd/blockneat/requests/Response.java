package com.csd.blockneat.requests;


public record Response(Signed<Either<Integer>> response) {
}
