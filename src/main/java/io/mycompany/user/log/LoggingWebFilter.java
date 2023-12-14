package io.mycompany.user.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LoggingWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(new LoggingWebExchange(exchange));
    }

}

@Slf4j
class LoggingWebExchange extends ServerWebExchangeDecorator {

    protected LoggingWebExchange(ServerWebExchange delegate) {
        super(delegate);
    }

    @Override
    public ServerHttpRequest getRequest() {
        return new LoggingRequestDecorator(super.getRequest(), getLogPrefix());
    }

    @Override
    public ServerHttpResponse getResponse() {
        return new ResponseLoggingInterceptor(super.getResponse(), getLogPrefix());
    }
}


