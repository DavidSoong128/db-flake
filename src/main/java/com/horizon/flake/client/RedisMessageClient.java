package com.horizon.flake.client;

import com.horizon.flake.common.Constants;
import com.horizon.flake.core.EventSqlHandler;
import com.horizon.flake.queue.EventWorkHandler;
import com.horizon.flake.queue.RingBufferQueue;
import com.horizon.flake.store.DataSourcePool;
import com.horizon.flake.util.JsonUtil;
import com.horizon.flake.util.RedisSingleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 *    handle the message from redis queue
 * </pre>
 * @author : David.Song/Java Engineer
 * @date : 2016/1/15 17:47
 * @see
 * @since : 1.0.0
 */
public class RedisMessageClient implements MessageClient{

    private Logger logger = LoggerFactory.getLogger(RedisMessageClient.class);

    private ExecutorService executorPool;
    private RingBufferQueue ringBufferQueue;
    private EventSqlHandler sqlHandler;

    public RedisMessageClient(){
        sqlHandler = EventSqlHandler.sqlHandler();
        ringBufferQueue = new RingBufferQueue(new EventWorkHandler(sqlHandler),Constants.N_THREADS,
                                                                               Constants.BUFFER_SIZE);
        executorPool = Executors.newFixedThreadPool(1);
    }

    public void startRead(){
        final Transfer transfer = new Transfer();
        executorPool.submit(new Runnable() {
            @Override
            public void run() {
                //1: 通过节点序列号node.seq路由处理对应的队列数据
                String routeQueueName = Constants.FLAKE_REDIS_QUEUE + Constants.FLAKE_NODE_SEQUENCE;
                logger.info("read queueName {}", routeQueueName);
                try {
                    //2: 通过路由队列名称，处理该队列的数据
                    transfer.beginHandle(routeQueueName);
                } catch (InterruptedException e) {
                    logger.error("stop read queue thread error ", e);
                }
            }
        });
    }

    public void stopRead(){
        executorPool.shutdown();
        ringBufferQueue.shutdown();
        sqlHandler.stopHandle();
    }

    class Transfer{

        private long startCheckTime = System.currentTimeMillis();
        private void beginHandle(String routQueueName) throws InterruptedException {
            while(true){
                try{
                    //默认30s检查下数据库链接
                    if(System.currentTimeMillis() - startCheckTime > 30000){
                        if(!this.checkDbIsConnect()){
                            Thread.sleep(Constants.QUEUE_SLEEP_INTERVAL);
                            continue;
                        }
                    }
                    Object dbEvent = RedisSingleHandler.redisHolder().rPopDBEvent(routQueueName);
                    if(dbEvent == null){
                        logger.debug("queue {} is empty,wait {} ms",routQueueName, Constants.QUEUE_SLEEP_INTERVAL);
                        Thread.sleep(Constants.QUEUE_SLEEP_INTERVAL);
                        continue;
                    }
                    logger.info("receive event message {} ", JsonUtil.ObjectToJson(dbEvent));
                    ringBufferQueue.publish(dbEvent);
                }catch(Exception ex){
                    logger.error("read queue data ex",ex);
                }
            }
        }
        /**
         * 在从redis queue中读取数据的时候，如果
         * 此时发现DB链接不上，那么存储在队列，不进行处理，避免数据丢失
         * @return
         */
        private boolean checkDbIsConnect(){
            Connection conn = null;
            try{
                conn = DataSourcePool.poolHolder().getConnection();
                if(conn == null || conn.isClosed()){
                    logger.error("can`t get db con and stop read data from queue");
                    return false;
                }
                //如果数据库一直链接不上，那么就一直检查链接
                startCheckTime = System.currentTimeMillis();
                return true;
            }catch(Exception ex){
                logger.error("getConnection error ",ex);
                return false;
            }finally{
                //这里必须将链接关闭，不然导致链接资源浪费
                DataSourcePool.poolHolder().closeConn(conn);
            }
        }
    }
}
