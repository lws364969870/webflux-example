package com.lws.example.service;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Test2 {

    public static void main(String[] args) {
        AtomicInteger subscribeTime = new AtomicInteger(1);
        //实现这一的效果，返回的数据流为1~5乘以当前subscribe的次数
        Supplier<? extends Publisher<Integer>> supplier = () -> {
            Integer[] array = {1, 2, 3, 4, 5};
            int currentTime = subscribeTime.getAndIncrement();
            for (int i = 0; i < array.length; i++) {
                array[i] *= currentTime;
            }
            return Flux.fromArray(array);
        };

        Flux<Integer> deferedFlux = Flux.defer(supplier);

        subscribe(deferedFlux, subscribeTime);
        subscribe(deferedFlux, subscribeTime);
        subscribe(deferedFlux, subscribeTime);
    }
    private static void subscribe(Flux<Integer> deferedFlux, AtomicInteger subscribeTime) {
        System.out.println("Subscribe time is "+subscribeTime.get());
        deferedFlux.subscribe(System.out::println);
    }
}
