package com.lws.example.pptdemo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * 常用线程方法
 */
@Slf4j
public class FluxSchedulerDemo {

    public static void main(String[] args) {
        publishOn();
        log.info("---------------------------------------------------------------------------------------------");
        subscribeOn();
    }

    /**
     * 让之后的操作在Scheduler提供的线程中执行
     */
    @SneakyThrows
    public static void publishOn() {

        Flux.range(1, 2).log()
                .map(i -> {
                    log.info("Map 1, the value map to: {}", i * i);
                    return i * i;
                })
                // 让之后的操作在Scheduler提供的线程中执行
                .publishOn(Schedulers.newSingle("线程A"))
                .map(i -> {
                    log.info("Map 2, the value map to: {}", -i);
                    return -i;
                })
                // 让之后的操作在Scheduler提供的线程中执行
                .publishOn(Schedulers.newParallel("线程B", 4))
                .map(i -> {
                    log.info("Map 3, the value map to: {}", i + 2);
                    return (i + 2) + "";
                })
                .subscribe();
    }

    /**
     * 让之前的操作在Scheduler提供的线程中执行
     */
    @SneakyThrows
    public static void subscribeOn() {

        Flux.range(1, 2).log()
                .map(i -> {
                    log.info("Map 1, the value map to: {}", i * i);
                    return i * i;
                })
                // 让之前的操作在Scheduler提供的线程中执行
                .subscribeOn(Schedulers.newSingle("线程C"))
                .map(i -> {
                    log.info("Map 2, the value map to: {}", -i);
                    return -i;
                })
                // 让之前的操作在Scheduler提供的线程中执行
                .subscribeOn(Schedulers.newParallel("线程D", 4))
                .map(i -> {
                    log.info("Map 3, the value map to: {}", i + 2);
                    return (i + 2) + "";
                })
                .subscribe();
    }
}
