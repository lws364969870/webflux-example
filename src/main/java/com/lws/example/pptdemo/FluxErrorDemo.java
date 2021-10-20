package com.lws.example.pptdemo;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 * 常用异常处理方法
 */
@Slf4j
public class FluxErrorDemo {

    public static void main(String[] args) {
        onErrorReturn();
        onError();
        retry();
    }

    /**
     * 静态fallback值
     */
    public static void onErrorReturn() {
        Flux<Integer> flux = Flux.just(0)
                .map(i -> 1 / i)
                //异常时返回0
                .onErrorReturn(0);
        print("onErrorReturn()", flux);
    }

    /**
     * 根据异常类型选择doOnError方法
     */
    public static void onError() {
        Flux<String> flux = Flux.just("0", "1", "2", "abc", "3")
                .map(i -> Integer.parseInt(i) + "")
                .doOnError(RuntimeException.class, e -> {
                    System.err.println("发生了RuntimeException");
                    e.printStackTrace();
                })
                .doOnError(NumberFormatException.class, e -> {
                    System.err.println("发生了NumberFormatException");
                    e.printStackTrace();
                })
                .onErrorReturn("System exception");
        //因为异常类型为NumberFormatException，此处应打印字符串发生了NumberFormatException
        //又因为doOnError不会阻止异常传播，所以onErrorReturn会执行，返回字符串System exception
        try {
            print("onError()", flux);
        } catch (Exception e) {

        }
    }

    /**
     * 重试
     */
    public static void retry() {
        //默认异常retry
        Flux<String> flux = Flux.just("0", "1", "2", "abc")
                .map(i -> Integer.parseInt(i) + "")
                .retry(2);
        flux.subscribe(newSub());

        //带条件判断的retry
        System.out.println("-------------------------------------------------");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flux = Flux.just("0", "1", "2", "abc")
                .map(i -> Integer.parseInt(i) + "")
                .retry(1);

        flux.subscribe(newSub());
    }

    private static Subscriber<String> newSub() {
        return new BaseSubscriber<String>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("start");
                request(1);
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println("get value is " + Integer.parseInt(value));
                request(1);
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Complete");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.err.println(throwable.getMessage());
            }
        };
    }

    public static void print(String functionName, Flux flux) {
        log.info("------------------------------------------------------------------------------------------------------------------");
        log.info("{} example for subscribe with consumer", functionName);
        flux.subscribe(data -> {
            log.info("{} print：" + data, functionName);
        });
    }
}
