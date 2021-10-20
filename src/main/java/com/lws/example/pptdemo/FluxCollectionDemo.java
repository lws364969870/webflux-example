package com.lws.example.pptdemo;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 常用集合方法
 */
@Slf4j
public class FluxCollectionDemo {

    public static void main(String[] args) {
        reduce();
    }

    /**
     * 将流中所有元素反复结合起来，来得到一个值
     */
    public static void reduce() {
        Flux<Integer> flux = Flux.range(0, 100);

        //使用迭代方式求和
        final AtomicInteger sum = new AtomicInteger(0);
        flux.subscribe(integer ->
                sum.getAndAdd(integer)
        );
        System.out.println(sum.get());

        //reduce方式求和
        flux.reduce((i, j) -> i + j)
                .subscribe(System.out::println);

        //reduce方式求和，并且指定reduce的初始值
        flux.reduce(100, (i, j) -> i + j)
                .subscribe(System.out::println);
    }
}
