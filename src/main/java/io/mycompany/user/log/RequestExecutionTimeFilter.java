package io.mycompany.user.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RequestExecutionTimeFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        StopWatch stopWatch = StopWatch.createStarted();
        return chain.filter(exchange)
                .doOnSuccess(aVoid ->
                        log.info("{} Request execution time {}",
                                exchange.getLogPrefix(),
                                stopWatch.formatTime())
                );
    }
}
