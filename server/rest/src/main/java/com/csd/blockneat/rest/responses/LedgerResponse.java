package com.csd.blockneat.rest.responses;

import com.csd.blockneat.application.commands.WriteCommand;

import java.util.List;

public record LedgerResponse(List<WriteCommand> commands) {
}
