package com.twq.rpcFrame.netty.ThreadPool;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * @Author: tangwq
 * @Description:
 */
public class SampleThreadPool {

    // 成员变量
    private volatile static ExecutorService executor;

    //单例模式创建
    public ExecutorService getExecutorService(){
        if(executor == null){
            synchronized (this){
                if(executor == null){
                    int availableProcessors = Runtime.getRuntime().availableProcessors();
                    int maximumPoolSize = availableProcessors * 4;
                    int queueCapacity = availableProcessors * 100;
                    long keepAliveTime = 60L;
                    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueCapacity);
                    executor = new ThreadPoolExecutor(
                            availableProcessors,
                            maximumPoolSize,
                            keepAliveTime,
                            TimeUnit.SECONDS,
                            workQueue,
                            new ThreadPoolExecutor.DiscardPolicy());

                    // 关闭客户端(关闭后台线程) 这是程序出错时会自动调用 里面的方法。
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        if(Objects.nonNull(executor)){
                            executor.shutdown();
                        }
                    }));
                }
            }
        }
        return executor;
    }
}
