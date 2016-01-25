package com.horizon.flake.threadpool;

import com.horizon.flake.common.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 10:18
 * @see
 * @since : 1.0.0
 */
public class ExecuteThreadPool {

    private Map<Integer,ExecutorService> threadPoolMap;

    private ExecuteThreadPool(){
        threadPoolMap = new HashMap<>();
        for (int i = 0; i <= Constants.FLAKE_THREAD_POOL_SIZE; i++) {
            //这里之所以为单线程，是为了保证处理的顺序性
            threadPoolMap.put(i, Executors.newFixedThreadPool(1));
        }
    }

    private static class ThreadPoolHolder{
        private static ExecuteThreadPool threadPool = new ExecuteThreadPool();
    }

    public static ExecuteThreadPool poolHolder(){
        return ThreadPoolHolder.threadPool;
    }

    public ExecutorService getThreadPool(Integer threadId){
        return threadPoolMap.get(threadId);
    }

    public void shutdown(){
        for (int i = 0; i <= Constants.FLAKE_THREAD_POOL_SIZE; i++) {
            threadPoolMap.get(i).shutdown();
        }
        threadPoolMap.clear();
    }
}
