package com.horizon.flake.client;

import com.horizon.flake.common.Constants;
import com.horizon.flake.core.EventSqlHandler;
import com.horizon.flake.queue.EventWorkHandler;
import com.horizon.flake.queue.RingBufferQueue;
import com.horizon.flake.store.DataSourcePool;
import com.horizon.flake.util.FstSerializer;
import com.horizon.flake.util.JsonUtil;
import com.horizon.mqclient.api.ConsumerResult;
import com.horizon.mqclient.api.Message;
import com.horizon.mqclient.api.MessageHandler;
import com.horizon.mqclient.api.TopicWithPartition;
import com.horizon.mqclient.core.consumer.KafkaHighConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * <pre>
 *     handle the message from kafka mq
 * </pre>
 *
 * @author : David.Song/Java Engineer
 * @date : 2016/1/15 17:48
 * @see
 * @since : 1.0.0
 */
public class KafkaMessageClient implements MessageClient {

    private Logger logger = LoggerFactory.getLogger(KafkaMessageClient.class);

    private KafkaHighConsumer highConsumer;
    private RingBufferQueue ringBufferQueue;
    private EventSqlHandler sqlHandler;

    public KafkaMessageClient() {
        highConsumer = KafkaHighConsumer.kafkaHighConsumer();
        sqlHandler   = EventSqlHandler.sqlHandler();
        ringBufferQueue = new RingBufferQueue(new EventWorkHandler(sqlHandler),
                                              Constants.N_THREADS,
                                              Constants.BUFFER_SIZE);
    }

    @Override
    public void startRead() {
        highConsumer.subscribe(Constants.FLAKE_KAFKA_TOPIC, new ReceiveMessageHandler());
        logger.info("subscribe topic {}", Constants.FLAKE_KAFKA_TOPIC);
    }

    @Override
    public void stopRead() {
        highConsumer.close();
        ringBufferQueue.shutdown();
        sqlHandler.stopHandle();
    }


    class ReceiveMessageHandler implements MessageHandler {
        private FstSerializer fstSerializer = new FstSerializer();
        private long startCheckTime = System.currentTimeMillis();
        @Override
        public void handleMessage(ConsumerResult result) {
            try {
                //默认30s检查下数据库链接
                if(System.currentTimeMillis() - startCheckTime > 30000 &&
                        !this.checkDbIsConnect()){
                    highConsumer.pauseConsume(new TopicWithPartition(result.getTopic(),
                            result.getPartition()));
                    logger.info("db can`t connect, pause consume topic {} partition {}",
                            result.getTopic(),result.getPartition());

                    while(!this.checkDbIsConnect()){
                        logger.info("db can`t connect,sleep interval time {}",
                                Constants.QUEUE_SLEEP_INTERVAL);
                        Thread.sleep(Constants.QUEUE_SLEEP_INTERVAL);
                    }

                    highConsumer.resumeConsume(new TopicWithPartition(result.getTopic(),
                            result.getPartition()));
                    logger.info("db connect again, resume consume topic {} partition {}",
                            result.getTopic(),result.getPartition());
                }

                Message message = result.getValue();
                if(message == null){
                    throw new IllegalArgumentException("result getValue is null");
                }

                byte[] body = message.getMessageByte();
                if(body == null || body.length == 0){
                    throw new IllegalArgumentException("result getMessageByte is null");
                }

                Object dbEvent = fstSerializer.deserialize(body);
                ringBufferQueue.publish(dbEvent);
                logger.info("publish event {}", JsonUtil.ObjectToJson(dbEvent));

            } catch (Exception ex) {
                logger.error("", ex);
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
