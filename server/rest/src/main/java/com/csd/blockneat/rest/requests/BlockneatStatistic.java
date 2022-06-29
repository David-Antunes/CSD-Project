package com.csd.blockneat.rest.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public record BlockneatStatistic(@JsonProperty("transactions") int transactions,
                                 @JsonProperty("minedBlockLatency") List<Long> minedBlockLatency,
                                 @JsonProperty("transactionLatency") List<Long> transactionLatency,
                                 @JsonProperty("avgMinedBlock") float avgMinedBlock,
                                 @JsonProperty("avgTransactionLatency")float avgTransactionLatency) {
}
