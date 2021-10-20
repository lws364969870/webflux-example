package com.lws.example.pptdemo;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * 常用调试方法
 */
@Slf4j
public class FluxDebugDemo {

    public static void main(String[] args) {
        log();
        expectNext();
    }

    /**
     * 输出Publisher接收到信号的每一步的信息
     */
    public static void log() {
        Flux.just(1, 2, 3, 4, 5)
                //日志记录详细的执行步骤
                .log()
                .subscribe();
    }

    /**
     * 断言
     */
    public static void expectNext() {
        StepVerifier.create(Flux.just("one", "two", "three"))
                //依次校验每一步的数据是否符合预期
                .expectNext("one")
                .expectNext("two")
                //不满足预期，抛出异常
                .expectNext("three")
                //校验Flux流是否按照预期正常关闭
                .expectComplete()
                //启动
                .verify();
    }


}
