package com.csd.blockneat.application.entities;

import com.csd.blockneat.application.commands.WriteCommand;

import java.io.Serializable;
import java.util.List;

public record Block(int id, int nonce, String previousBlockHash, List<WriteCommand> transactions) implements Serializable {
}
