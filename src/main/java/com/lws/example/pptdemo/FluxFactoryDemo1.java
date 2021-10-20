package com.lws.example.pptdemo;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * 常用静态工厂方法
 */
@Slf4j
public class FluxFactoryDemo1 {

    public static void main(String[] args) {
        just();
        fromIterable();
        fromSteam();
        fromArray();
        range();
        empty();
        error();
        never();
    }

    /**
     * just方法接收若干个参数，使用这些参数构造一个Flux流
     */
    public static void just() {
        Flux<String> flux = Flux.just("a", "b", "c");
        print("just()", flux);
    }

    /**
     * 通过Iterable构造Flux流
     */
    public static void fromIterable() {
        List<String> list = Arrays.asList("a", "b", "c");
        // 等价 Flux.just("a", "b", "c")
        Flux<String> flux = Flux.fromIterable(list);
        print("fromIterable()", flux);
    }

    /**
     * 通过Steam构造Flux流
     */
    public static void fromSteam() {
        List<String> list = Arrays.asList("a", "b", "c");
        Flux<String> flux = Flux.fromStream(list.stream());
        print("fromSteam()", flux);
    }

    /**
     * 通过数组构造Flux流
     */
    public static void fromArray() {
        String[] array = new String[]{"a", "b", "c"};
        // 等价 Flux.just("a", "b", "c")
        Flux<String> flux = Flux.fromArray(array);
        print("fromArray()", flux);
    }

    /**
     * range(int start, int count)构造了一个Flux<Integer>流
     * 返回从 [start,start+count) 区间的整数。
     * 注意，range方法会处理整数溢出的场景，在溢出时抛出异常。
     */
    public static void range() {
        try {
            Flux flux1 = Flux.range(1, 5);
            print("range1()", flux1);

            // 测试int溢出
            Flux flux2 = Flux.range(Integer.MAX_VALUE, 5);
            print("range2()", flux2);
        } catch (Exception e) {
            log.error("range()方法异常：{}", e.getMessage());
        }
    }

    /**
     * 返回一个没有任何数据、异常的流。
     * onSubscribe回调(√)
     * onNext回调(×)
     * onComplete回调(√)
     * onError回调(×)
     */
    public static void empty() {
        log.info("------------------------------------------------------------------------------------------------------------------");
        log.info("{} example for subscribe with consumer", "empty()");
        Flux.empty().subscribe(getSubscriber());
    }

    /**
     * 返回一个没有任何数据，只有异常的流程
     * onSubscribe回调(√)
     * onNext回调(×)
     * onComplete回调(×)
     * onError回调(√)
     */
    public static void error() {
        log.info("------------------------------------------------------------------------------------------------------------------");
        log.info("{} example for subscribe with consumer", "error()");
        Flux.error(new RuntimeException("自定义异常")).subscribe(getSubscriber());
    }

    /**
     * 返回一个不会发送任何通知额流程
     * onSubscribe回调(√)
     * onNext回调(×)
     * onComplete回调(×)
     * onError回调(×)
     */
    public static void never() {
        log.info("------------------------------------------------------------------------------------------------------------------");
        log.info("{} example for subscribe with consumer", "never()");
        Flux.never().subscribe(getSubscriber());
    }

    public static Subscriber getSubscriber() {
        Subscriber subscriber = new Subscriber() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("onSubscribe回调");
                subscription.request(1);
            }

            @Override
            public void onNext(Object o) {
                log.info("onNext回调，value is " + o);
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("onError回调，exception message is " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                log.info("onComplete回调");
            }
        };
        return subscriber;
    }

    public static void print(String functionName, Flux flux) {
        log.info("------------------------------------------------------------------------------------------------------------------");
        log.info("{} example for subscribe with consumer", functionName);
        flux.subscribe(data -> {
            log.info("{} print：" + data, functionName);
        });
    }


}
