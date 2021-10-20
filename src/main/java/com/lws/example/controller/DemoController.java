package com.lws.example.controller;

import com.lws.example.dto.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@RestController
public class DemoController {

    /**
     * Mono演示
     *
     * @return
     */
    @GetMapping("/getData")
    public Mono<String> getData() {
        return Mono.just("monoResult");
    }

    /**
     * Flux演示
     *
     * @return
     */
    @GetMapping(value = "/getFlux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getThreadData() {
        List<String> list = Arrays.asList("a", "b", "c", "d", "e");
        Stream stream = list.stream().map(var -> {
            // 模拟业务处理过程，每次耗时一秒
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return var;
        });
        return Flux.fromStream(stream);
    }

    // 传统数据处理
    @GetMapping("/getUser")
    public List<User> getUser() {
        User user1 = new User(1, "张三");
        User user2 = new User(2, "李四");
        return Arrays.asList(user1, user2);
    }

    // 响应式数据处理
    @GetMapping("/getFluxUser")
    public Flux<User> getFluxUser() {
        User user1 = new User(1, "张三");
        User user2 = new User(2, "李四");
        return Flux.just(user1, user2);
    }

}
