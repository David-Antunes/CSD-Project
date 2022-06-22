package com.csd.blockneat.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.blockneat.application.ledger.commands.GetLedgerQuery;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import com.csd.blockneat.rest.responses.LedgerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledger")
public class LedgerController {


    private final Pipeline pipeline;

    @Autowired
    public LedgerController(@Qualifier(PipelinrConfig.CONTROLLER_PIPELINE) Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @GetMapping()
    public LedgerResponse getLedger() {
        return new LedgerResponse(new GetLedgerQuery().execute(pipeline));
    }

}
