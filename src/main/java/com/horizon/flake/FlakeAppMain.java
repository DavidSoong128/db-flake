package com.horizon.flake;

import com.horizon.flake.client.KafkaMessageClient;
import com.horizon.flake.client.MessageClient;
import com.horizon.flake.client.RedisMessageClient;
import com.horizon.flake.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/15 17:39
 * @see
 * @since : 1.0.0
 */
public class FlakeAppMain {

    public static Logger logger = LoggerFactory.getLogger(FlakeAppMain.class);

    private static volatile boolean running = true;

    private static MessageClient messageClient;

    public static void main(String[] args) {
        startApp();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("service " + FlakeAppMain.class.getSimpleName() + " stopped!");
                synchronized (FlakeAppMain.class) {
                    running = false;
                    stopApp();
                    FlakeAppMain.class.notify();
                }
            }
        });
        synchronized (FlakeAppMain.class) {
            while (running) {
                try {
                    FlakeAppMain.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }

    private static void startApp() {
        if(Constants.QUEUE_TYPE.equals(Constants.REDIS_QUEUE_TYPE)){
            messageClient = new RedisMessageClient();
        }else if(Constants.KAFKA_QUEUE_TYPE.equals(Constants.QUEUE_TYPE)){
            messageClient = new KafkaMessageClient();
        }else{
            throw new IllegalArgumentException("queue.type config not correct!");
        }
        messageClient.startRead();
        logger.info("handle message client start success !");
    }

    private static void stopApp(){
        messageClient.stopRead();
    }
}
