package com.csd.blockneat.application.entities;

import java.io.Serializable;

public record ValidatedBlock(Block block, String hash, long timestamp) implements Serializable {
}
