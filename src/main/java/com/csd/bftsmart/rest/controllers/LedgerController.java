package com.csd.bftsmart.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.bftsmart.application.ledger.commands.GetLedgerQuery;
import com.csd.bftsmart.infrastructure.persistence.InMemoryLedger;
import com.csd.bftsmart.infrastructure.pipelinr.PipelinrConfig;
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
    public InMemoryLedger getLedger() {return new GetLedgerQuery().execute(pipeline);}

}
