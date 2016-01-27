package com.horizon.flake.client;

import com.horizon.flake.common.Constants;
import com.horizon.flake.core.EventSqlHandler;
import com.horizon.flake.queue.EventWorkHandler;
import com.horizon.flake.queue.RingBufferQueue;

/**
 * 关联消息收取客户端、sql处理器、缓存队列
 * @author : David.Song/Java Engineer
 * @date : 2016/1/27 9:50
 * @see
 * @since : 1.0.0
 */
public class DBFlakeContext {
    //队列IO和DB IO之间的缓冲区
    private RingBufferQueue   ringBufferQueue;
    //sql消息处理器
    private EventSqlHandler   sqlHandler;
    //消息处理抽象接口，目前主要支持redis和kafka
    private MessageClient     messageClient;
    //使用的客户端队列类型，kafka,redis
    private String            queueType;

    /**
     * 根据队列类型控制是从kafka还是redis读取数据
     * @param queueType
     */
    public DBFlakeContext(String queueType){
        this.queueType = queueType;
        initContext();
    }

    /**
     * 默认从redis读取数据
     */
    public DBFlakeContext(){
        this.queueType = Constants.REDIS_QUEUE_TYPE;
        initContext();
    }

    /**
     * 启动上下文，开启处理线程
     */
    public void startContext(){
        if(Constants.REDIS_QUEUE_TYPE.equals(queueType)){
            messageClient = new RedisMessageClient(ringBufferQueue);
        }else if(Constants.KAFKA_QUEUE_TYPE.equals(queueType)){
            messageClient = new KafkaMessageClient(ringBufferQueue);
        }else{
            throw new IllegalArgumentException("queue.type config not correct!");
        }
        messageClient.startProcess();
    }

    /**
     * 初始化处理器以及队列
     */
    private void initContext() {
        sqlHandler = EventSqlHandler.sqlHandler();
        ringBufferQueue = new RingBufferQueue(new EventWorkHandler(sqlHandler),
                          Constants.N_THREADS, Constants.BUFFER_SIZE);
    }

    /**
     * 关闭上下文，释放资源
     */
    public void stopContext(){
        messageClient.stopProcess();
        ringBufferQueue.shutdown();
        sqlHandler.stopHandle();
    }
}
