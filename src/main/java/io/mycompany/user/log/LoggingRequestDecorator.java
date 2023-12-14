package io.mycompany.user.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import static io.mycompany.user.log.Utils.payload;

@Slf4j
class LoggingRequestDecorator extends ServerHttpRequestDecorator {
    private final String logPrefix;

    public LoggingRequestDecorator(ServerHttpRequest delegate, String logPrefix) {
        super(delegate);
        this.logPrefix = logPrefix;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        if (log.isDebugEnabled()) {
            return super.getBody().doOnNext(dataBuffer ->
                    payload(dataBuffer)
                            .ifPresent(p ->
                                    log.info("{} Request: method={}, uri={}, payload={}",
                                            logPrefix,
                                            getDelegate().getMethod(),
                                            getDelegate().getPath(), p
                                    ))
            );
        } else {
            return super.getBody();
        }
    }

}
