package com.csd.blockneat.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.blockneat.application.Either;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.mining.commands.GetBlockToMineQuery;
import com.csd.blockneat.application.mining.commands.ProposeBlockCommand;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import com.csd.blockneat.rest.exceptions.HandleWebExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping("/mining")
public class MiningController {

    private final Pipeline pipeline;

    @Autowired
    public MiningController(@Qualifier(PipelinrConfig.CONTROLLER_PIPELINE) Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @GetMapping
    public byte[] getBlockToMine() {
        try (var byteOut = new ByteArrayOutputStream();
             var objOut = new ObjectOutputStream(byteOut)) {

            objOut.writeObject(new GetBlockToMineQuery().execute(pipeline).right());

            objOut.flush();
            byteOut.flush();
            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping
    public void proposeBlock(@RequestBody byte[] serializedBlock) {
        ValidatedBlock block;
        try (var byteIn = new ByteArrayInputStream(serializedBlock);
             var objIn = new ObjectInputStream(byteIn)) {
            block = (ValidatedBlock) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        HandleWebExceptions.resultOrException((new ProposeBlockCommand(block).execute(pipeline)));
    }
}

