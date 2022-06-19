package com.csd.bftsmart.rest.responses;

import com.csd.bftsmart.application.commands.WriteCommand;

import java.util.List;

public record LedgerResponse(List<WriteCommand> commands) {
}
