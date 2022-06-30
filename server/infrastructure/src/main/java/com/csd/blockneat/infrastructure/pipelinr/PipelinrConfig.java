package com.csd.blockneat.infrastructure.pipelinr;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Pipeline;
import an.awesome.pipelinr.Pipelinr;
import com.csd.blockneat.application.commands.CommandTypes;
import com.csd.blockneat.application.middlewares.LedgerPersistable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
public class PipelinrConfig {

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
    @ConditionalOnExpression("${bftsmart.enabled:false} and ${blockmess.enabled:false}")
    Pipeline controllerLocalReadWritePipeline(@Qualifier("readWritePipeline") Pipeline pipeline) {
        return pipeline;
    }

    @ConditionalOnProperty(name = "bftsmart.enabled")
    public static class BftSmart {

        public static final String APP_READ = "bft_smart_" + CommandTypes.APP_READ;
        public static final String APP_WRITE = "bft_smart_" + CommandTypes.APP_WRITE;
        @Bean
        @Qualifier(APP_READ)
        Pipeline bftSmartReadPipeline(@Qualifier(APP_READ) ObjectProvider<Command.Handler> readCommandHandlers) {
            return new Pipelinr()
                    .with(readCommandHandlers::stream);
        }

        @Bean
        @Qualifier(APP_WRITE)
        Pipeline bftSmartReadWritePipeline(@Qualifier(APP_READ) ObjectProvider<Command.Handler> readCommandHandlers,
                                           @Qualifier(APP_WRITE) ObjectProvider<Command.Handler> writeCommandHandlers,
                                           @Autowired LedgerPersistable ledgerMiddleware) {
            return new Pipelinr()
                    .with(() -> Stream.concat(readCommandHandlers.stream(), writeCommandHandlers.stream()))
                    .with(() -> Stream.of(ledgerMiddleware));
        }

        @Bean
        @Qualifier(CONTROLLER_PIPELINE)
        Pipeline controllerBftSmartReadWritePipeline(@Qualifier(APP_READ) ObjectProvider<Command.Handler> readCommandHandlers,
                                                     @Qualifier(APP_WRITE) ObjectProvider<Command.Handler> writeCommandHandlers) {
            return new Pipelinr()
                    .with(() -> Stream.concat(readCommandHandlers.stream(), writeCommandHandlers.stream()));
        }
    }

    @ConditionalOnProperty(name = "blockmess.enabled")
    public static class Blockmess {

        public static final String APP_WRITE = "blockmess_" + CommandTypes.APP_WRITE;

        @Bean
        @Qualifier(APP_WRITE)
        Pipeline blockmessReadWritePipeline(@Qualifier(APP_WRITE) ObjectProvider<Command.Handler> writeCommandHandlers,
                                           @Autowired LedgerPersistable ledgerMiddleware) {
            return new Pipelinr()
                    .with(writeCommandHandlers::stream)
                    .with(() -> Stream.of(ledgerMiddleware));
        }

        @Bean
        @Qualifier(CONTROLLER_PIPELINE)
        Pipeline controllerBlockmessReadWritePipeline(@Qualifier(APP_WRITE) ObjectProvider<Command.Handler> writeCommandHandlers) {
            return new Pipelinr()
                    .with(writeCommandHandlers::stream);
        }
    }

}
