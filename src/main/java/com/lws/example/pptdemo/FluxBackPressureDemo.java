package com.lws.example.pptdemo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 背压
 */
@Slf4j
public class FluxBackPressureDemo {

    @SneakyThrows
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //可以观察到明显的限流
        Flux<Long> flux = Flux.interval(Duration.ofMillis(50))
                .take(50)
                .doOnComplete(() -> countDownLatch.countDown());
        flux.subscribe(new MyLimitedSubscriber(5));
        countDownLatch.await();

        //使用比count还大的limiter，相当于不限流
        System.out.println("use big limiter2");
        Flux.interval(Duration.ofMillis(50))
                .take(20)
                .subscribe(new MyLimitedSubscriber(100));


    }
}

class MyLimitedSubscriber<T> extends BaseSubscriber<T> {
    private long mills;
    private ThreadPoolExecutor threadPool;
    private int maxWaiting;
    private final Random random = new Random();

    public MyLimitedSubscriber(int maxWaiting) {
        this.maxWaiting = maxWaiting;
        this.threadPool = new ThreadPoolExecutor(
                1, 1, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(maxWaiting));
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        this.mills = System.currentTimeMillis();
        requestNextDatas();
    }

    @Override
    protected void hookOnComplete() {
        long now = System.currentTimeMillis();
        long time = now - this.mills;
        System.out.println("cost time:" + time / 1000 + " seconds");
        this.threadPool.shutdown();
    }

    @Override
    protected void hookOnNext(T value) {
        //提交任务
        this.threadPool.execute(new MyTask(value));
        //请求数据
        requestNextDatas();
    }


    private void requestNextDatas() {
        //计算请求的数据的范围
        int requestSize = this.maxWaiting - this.threadPool.getQueue().size();
        if (requestSize > 0) {
            System.out.println("客户端通知还能处理request数量为：" + requestSize);

            request(requestSize);
            return;
        } else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            requestNextDatas();
        }

    }

    class MyTask<T> implements Runnable {
        private T data;

        public MyTask(T data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(random.ints(100, 500).findFirst().getAsInt());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("data is " + data);
            //可以在处理完成数据之后，立刻进行请求，此时Subscriber肯定是能够可以可靠处理数据的
            //requestNextDatas()或者调用BaseSubscriber#request(1)
        }

    }
}