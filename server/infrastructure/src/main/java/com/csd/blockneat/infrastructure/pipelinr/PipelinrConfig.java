package com.csd.blockneat.infrastructure.pipelinr;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import com.csd.blockneat.application.CommandTypes;
import com.csd.blockneat.application.middlewares.LedgerPersistable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
public class PipelinrConfig {

    public static final String BFT_SMART_APP_READ = "bft_smart_" + CommandTypes.APP_READ;
    public static final String BFT_SMART_APP_WRITE = "bft_smart_" + CommandTypes.APP_WRITE;
    public static final String CONTROLLER_PIPELINE = "controller_pipeline";

    @Bean
    @Qualifier(CommandTypes.APP_READ)
    Pipeline readPipeline(@Qualifier(CommandTypes.APP_READ) ObjectProvider<Command.Handler> commandHandlers) {
        return new Pipelinr()
                .with(commandHandlers::stream);
    }

    @Bean
    @Qualifier(CommandTypes.APP_WRITE)
    Pipeline readWritePipeline(@Qualifier(CommandTypes.APP_READ) ObjectProvider<Command.Handler> readCommandHandlers,
                               @Qualifier(CommandTypes.APP_WRITE) ObjectProvider<Command.Handler> writeCommandHandlers,
                               @Autowired LedgerPersistable ledgerMiddleware) {
        return new Pipelinr()
                .with(() -> Stream.concat(readCommandHandlers.stream(), writeCommandHandlers.stream()))
                .with(() -> Stream.of(ledgerMiddleware));
    }

    @Bean
    @Qualifier(CONTROLLER_PIPELINE)
    @ConditionalOnProperty(name = "bftsmart.enabled", havingValue = "false", matchIfMissing = true)
    Pipeline controllerLocalReadWritePipeline(@Qualifier("readWritePipeline") Pipeline pipeline) {
        return pipeline;
    }

    @ConditionalOnProperty(name = "bftsmart.enabled")
    static class BftSmart {
        @Bean
        @Qualifier(BFT_SMART_APP_READ)
        Pipeline bftSmartReadPipeline(@Qualifier(BFT_SMART_APP_READ) ObjectProvider<Command.Handler> readCommandHandlers) {
            return new Pipelinr()
                    .with(readCommandHandlers::stream);
        }

        @Bean
        @Qualifier(BFT_SMART_APP_WRITE)
        Pipeline bftSmartReadWritePipeline(@Qualifier(BFT_SMART_APP_READ) ObjectProvider<Command.Handler> readCommandHandlers,
                                           @Qualifier(BFT_SMART_APP_WRITE) ObjectProvider<Command.Handler> writeCommandHandlers,
                                           @Autowired LedgerPersistable ledgerMiddleware) {
            return new Pipelinr()
                    .with(() -> Stream.concat(readCommandHandlers.stream(), writeCommandHandlers.stream()))
                    .with(() -> Stream.of(ledgerMiddleware));
        }

        @Bean
        @Qualifier(CONTROLLER_PIPELINE)
        Pipeline controllerBftSmartReadWritePipeline(@Qualifier("bftSmartReadWritePipeline") Pipeline pipeline) {
            return pipeline;
        }
    }

}
