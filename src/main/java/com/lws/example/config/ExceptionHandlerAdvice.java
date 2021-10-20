package com.lws.example.config;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * 全局异常处理
 */
@Component
public class ExceptionHandlerAdvice implements WebExceptionHandler {
    /**
     * Handle the given exception. A completion signal through the return value
     * indicates error handling is complete while an error signal indicates the
     * exception is still not handled.
     *
     * @param exchange the current exchange
     * @param ex       the exception to handle
     * @return {@code Mono<Void>} to indicate when exception handling is complete
     */
    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        return handle(ex).flatMap(it -> it.writeTo(exchange,
                        new HandlerStrategiesResponseContext(HandlerStrategies.withDefaults())))
                .flatMap(i -> Mono.empty());
    }


    private Mono<ServerResponse> handle(Throwable ex) {
        return createResponse(INTERNAL_SERVER_ERROR, ex);
    }

    // 组装异常对象
    private Mono<ServerResponse> createResponse(final HttpStatus httpStatus, Throwable ex) {
        ErrorResponseData data = new ErrorResponseData();
        data.setCode("error");
        data.setMsg(ex.getMessage());
        data.setStackMsg(ex.getMessage());
        return ServerResponse.status(httpStatus).syncBody(data);
    }

    @Data
    static class ErrorResponseData {

        private String code;
        private String msg;
        private String stackMsg;
    }

    /**
     * The type Handler strategies response context.
     */
    static class HandlerStrategiesResponseContext implements ServerResponse.Context {

        private HandlerStrategies handlerStrategies;

        /**
         * Instantiates a new Handler strategies response context.
         *
         * @param handlerStrategies the handler strategies
         */
        public HandlerStrategiesResponseContext(final HandlerStrategies handlerStrategies) {
            this.handlerStrategies = handlerStrategies;
        }

        /**
         * Return the {@link HttpMessageWriter}s to be used for response body conversion.
         *
         * @return the list of message writers
         */
        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return this.handlerStrategies.messageWriters();
        }

        /**
         * Return the  {@link ViewResolver}s to be used for view name resolution.
         *
         * @return the list of view resolvers
         */
        @Override
        public List<ViewResolver> viewResolvers() {
            return this.handlerStrategies.viewResolvers();
        }
    }


}
