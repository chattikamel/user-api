package io.mycompany.user.log;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Mono;

import static io.mycompany.user.log.Utils.payload;
import static reactor.core.publisher.Flux.from;

@Slf4j
public class ResponseLoggingInterceptor extends ServerHttpResponseDecorator {

    private final String logPrefix;

    public ResponseLoggingInterceptor(ServerHttpResponse delegate, String logPrefix) {
        super(delegate);
        this.logPrefix = logPrefix;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (log.isDebugEnabled()) {
            return super.writeWith(
                    from(body).doOnNext(dataBuffer ->
                            payload(dataBuffer)
                                    .ifPresent(p ->
                                            log.debug("{} Response status={}, payload={}",
                                                    logPrefix,
                                                    getStatusCode().value(), p)
                                    )
                    )

            );
        } else
            return super.writeWith(body);
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        if (log.isDebugEnabled()) {
            return super.writeAndFlushWith(
                    from(body).map(publisher ->
                            from(publisher).doOnNext(dataBuffer ->
                                    payload(dataBuffer)
                                            .map(String::trim)
                                            .filter(s -> !s.isBlank() && !"data:".equals(s))
                                            .ifPresent(p ->
                                                    log.debug("{} Response payload={}", logPrefix, p)
                                            )
                            )
                    )
            );
        } else
            return super.writeAndFlushWith(body);
    }

}
