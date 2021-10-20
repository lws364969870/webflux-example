package com.lws.example.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TestService {

    public static void main(String[] args) throws InterruptedException {

        // Create 创建一个Flux
//        createFlux();

        // Generate 创建一个Flux
//        generateFlux();

        interval();
    }

    /**
     * Create 创建一个Flux
     * 异步，可以多次next通知
     */
    public static void createFlux() {
        Flux<String> createFlux = Flux.create(fluxSink -> {
            // 通知
            fluxSink.next("a");
            // 通知
            fluxSink.next("b");
            // 结束
            fluxSink.complete();
        });
        // 订阅
        createFlux.subscribe(System.out::println);
        log.info("-----------------------------------------------------------------");
    }

    /**
     * Generate 创建一个Flux
     * 同步，只能next通知一次
     */
    public static void generateFlux() {
        Flux<String> generateFlux = Flux.generate(synchronousSink -> {
            // 通知
            synchronousSink.next("c");
            // 结束
            synchronousSink.complete();
        });
        // 订阅
        generateFlux.subscribe(System.out::println);
        log.info("-----------------------------------------------------------------");
    }

    /**
     * interval方法周期性生成从0开始的的Long。周期从delay之后启动，每隔period时间返回一个加1后的Long。
     * interval方法返回的Flux运行在另外的线程中，main线程需要休眠或者阻塞之后才能看到周期性的输出。
     */
    public static void interval() {
        Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(1)).subscribe(System.out::println);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
