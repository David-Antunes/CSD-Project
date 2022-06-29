package com.csd.blockneat.rest.controllers;

import an.awesome.pipelinr.Pipeline;
import com.csd.blockneat.application.commands.WriteCommand;
import com.csd.blockneat.application.entities.Block;
import com.csd.blockneat.application.entities.ValidatedBlock;
import com.csd.blockneat.application.ledger.commands.GetLedgerQuery;
import com.csd.blockneat.application.transactions.commands.LoadMoneyCommand;
import com.csd.blockneat.application.transactions.commands.SendTransactionCommand;
import com.csd.blockneat.infrastructure.pipelinr.PipelinrConfig;
import com.csd.blockneat.rest.requests.BlockneatStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {


    private final Pipeline pipeline;

    @Autowired
    public LedgerController(@Qualifier(PipelinrConfig.CONTROLLER_PIPELINE) Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @GetMapping()
    public List<ValidatedBlock> getLedger() {
        return new GetLedgerQuery().execute(pipeline);
    }

    @GetMapping("/statistics")
    public BlockneatStatistic getBlockneatStatistics() {
        List<ValidatedBlock> blockneat = getLedger();

        blockneat = blockneat.subList(1, blockneat.size());
        List<Long> mineBlockLatency = new LinkedList<>();
        List<Long> transactionLatency = new LinkedList<>();
        float avgTransactionLatency = 0.0f;
        float avgMinedBlock = 0.0f;
        int transactions = 0;

        for (ValidatedBlock vBlock : blockneat) {
            long blockTimestamp = vBlock.timestamp();
            mineBlockLatency.add(blockTimestamp);
            Block block = vBlock.block();
            transactions += block.transactions().size();
            for (WriteCommand wc : block.transactions()) {
                if (wc instanceof LoadMoneyCommand loadMoneyCommand) {
                    transactionLatency.add(vBlock.timestamp() - loadMoneyCommand.timestamp());
                } else if (wc instanceof SendTransactionCommand sendTransactionCommand)
                    transactionLatency.add(vBlock.timestamp() - sendTransactionCommand.timestamp());
            }
        }

        for (Long value : transactionLatency)
            avgTransactionLatency += value;
        avgTransactionLatency = avgTransactionLatency / transactions;


        List<Long> timeTillNextBlock = new LinkedList<>();
        for (int i = 0; i < mineBlockLatency.size() - 1; i++) {
            timeTillNextBlock.add(mineBlockLatency.get(i + 1) - mineBlockLatency.get(i));
        }
        mineBlockLatency = timeTillNextBlock;
        for (Long aLong : timeTillNextBlock) {
            avgMinedBlock += aLong;
        }
        avgMinedBlock = avgMinedBlock / timeTillNextBlock.size();
        return new BlockneatStatistic(transactions, mineBlockLatency, transactionLatency, avgMinedBlock, avgTransactionLatency);

    }


}
