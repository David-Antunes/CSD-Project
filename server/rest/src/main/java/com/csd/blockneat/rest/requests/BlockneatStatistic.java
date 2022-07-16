package com.csd.blockneat.rest.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
public record BlockneatStatistic(@JsonProperty("transactions") int transactions,
                                 @JsonProperty("minedBlockLatency") List<Long> minedBlockLatency,
                                 @JsonProperty("transactionLatency") List<Long> transactionLatency,
                                 @JsonProperty("avgMinedBlock") float avgMinedBlock,
                                 @JsonProperty("avgTransactionLatency")float avgTransactionLatency) {
}
