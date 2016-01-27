package com.horizon.flake.core;

import com.horizon.flake.store.SQLExecutor;
import com.horizon.flake.threadpool.ExecuteThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 10:23
 * @see
 * @since : 1.0.0
 */
public class EventSqlHandler {

    private Logger logger = LoggerFactory.getLogger(EventSqlHandler.class);

    private EventSqlParser    sqlParser   = EventSqlParser.sqlParser();
    private ExecuteThreadPool threadPool  = ExecuteThreadPool.poolHolder();
    private SQLExecutor       sqlExecutor = SQLExecutor.sqlHolder();

    private static class Holder {
        private static EventSqlHandler sqlHandler = new EventSqlHandler();
    }

    public static EventSqlHandler sqlHandler() {
        return Holder.sqlHandler;
    }

    public void sqlHandler(final Object dbEvent) {
        try {
            //根据规则路由到不同的线程池，线性执行
            Integer threadId = sqlParser.routeThreadId(dbEvent);
            //获取线程池
            ExecutorService executor = threadPool.getThreadPool(threadId);
            //解析sql，执行sql
            executor.execute(new Runnable() {
                public void run() {
                    sqlExecutor.executeSql(dbEvent);
                }
            });
        } catch (Exception e) {
            logger.error("execute sql exception", e);
        }
    }

    public void stopHandle() {
        threadPool.shutdown();
    }
}
