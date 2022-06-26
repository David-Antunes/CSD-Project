package com.csd.blockneat.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.mining.commands.GetBlockToMineQuery;
import com.csd.blockneat.application.mining.commands.ProposeBlockCommand;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mining")
public class MiningController {

    private final Pipeline pipeline;

    @Autowired
    public MiningController(@Qualifier(PipelinrConfig.CONTROLLER_PIPELINE) Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @GetMapping
    public Either<Block> getBlockToMine() {
        return new GetBlockToMineQuery().execute(pipeline);
    }

    @PostMapping
    public void proposeBlock(@RequestBody ValidatedBlock block) {
        new ProposeBlockCommand(block).execute(pipeline);
    }
}

