package com.lws.example.pptdemo;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 常用静态工厂方法2
 */
@Slf4j
public class FluxFactoryDemo2 {

    public static void main(String[] args) {
        interval();
        from();
        concant();
        merge();
        create();
    }

    /**
     * interval方法周期性生成从0开始的的Long。周期从delay之后启动，每隔period时间返回一个加1后的Long。
     * 注意，interval方法返回的Flux运行在另外的线程中，main线程需要休眠或者阻塞之后才能看到周期性的输出。
     */
    public static void interval() {
        Flux flux = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(1));
        print("interval()", flux);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将已有的Publisher包装为一个Flux流
     */
    public static void from() {
        Publisher<Integer> fluxPublisher = Flux.just(1, 2, 3);
        Publisher<Integer> monoPublisher = Mono.just(0);
        print("from() flux = ", Flux.from(fluxPublisher));
        print("from() mono = ", Flux.from(monoPublisher));
    }

    /**
     * 多个Publisher拼接为一个Flux,按顺序返回
     *
     */
    public static void concant() {
        Flux<Integer> sourceWithErrorNumFormat = Flux.just("1", "2", "3", "4", "5").map(
                str -> Integer.parseInt(str)
        );
        Flux<Integer> source = Flux.just("5", "6", "7", "8", "9").map(
                str -> Integer.parseInt(str)
        );
        // 等待所有的流处理完成之后，再将异常传播下去
        Flux<Integer> flux = Flux.concatDelayError(sourceWithErrorNumFormat, source);
        print("concant()", flux);
    }

    /**
     * 多个Publisher拼接为一个Flux,按优先生成数据返回
     *
     */
    public static void merge() {
        Flux<Long> flux1 = Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(1));
        Flux<Long> flux2 = Flux.interval(Duration.ofSeconds(2), Duration.ofSeconds(1));
        Flux<Long> flux = Flux.merge(flux1, flux2);
        print("merge()", flux);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将已有的异步事件流，包装为Flux流
     */
    public static void create(){
        Flux<String> flux = Flux.create(fluxSink -> {
            // 通知
            fluxSink.next("a");
            // 通知
            fluxSink.next("b");
            // 结束
            fluxSink.complete();
        });
        print("create()", flux);
    }

    public static void print(String functionName, Flux flux) {
        log.info("------------------------------------------------------------------------------------------------------------------");
        log.info("{} example for subscribe with consumer", functionName);
        flux.subscribe(data -> {
            log.info("{} print：" + data, functionName);
        });
    }
}
