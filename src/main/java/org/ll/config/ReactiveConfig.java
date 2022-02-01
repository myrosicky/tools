package org.ll.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration @Slf4j
public class ReactiveConfig
        //extends WebFluxConfigurationSupport
{

    @Bean
    public RouterFunction<ServerResponse> initRouterFunction() {
        return RouterFunctions.route()
                .GET("/imgs/{id}", serverRequest -> {
                    String name = serverRequest.pathVariable("id");
                    return ServerResponse.ok().bodyValue(name);
                }).build();
    }
}
