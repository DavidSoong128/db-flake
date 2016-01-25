package com.horizon.flake.common;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 10:18
 * @see
 * @since : 1.0.0
 */
public class Constants {

    public static final String JDBC_USERNAME = FlakeConfig.configHolder().getConfigValue(FlakeConfig.JDBC_USERNAME);
    public static final String JDBC_URL = FlakeConfig.configHolder().getConfigValue(FlakeConfig.JDBC_URL);
    public static final String JDBC_PASSWORD = FlakeConfig.configHolder().getConfigValue(FlakeConfig.JDBC_PASSWORD);
    public static final String JDBC_DRIVER = FlakeConfig.configHolder().getConfigValue(FlakeConfig.JDBC_DRIVER);


    public static final Integer JDBC_POOL_MAXIDLE = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.JDBC_POOL_MAXIDLE));
    public static final Integer JDBC_POOL_MINIDLE = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.JDBC_POOL_MINIDLE));
    public static final Integer JDBC_POOL_INITSIZE = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.JDBC_POOL_INITSIZE));
    public static final Integer JDBC_POOL_MAXACTIVE = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.JDBC_POOL_MAXACTIVE));
    public static final Integer FLAKE_THREAD_POOL_SIZE = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.FLAKE_THREAD_POOL_SIZE));
    public static final Long QUEUE_SLEEP_INTERVAL = Long.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.QUEUE_SLEEP_INTERVAL));
    public static final Integer FLAKE_NODE_SEQUENCE = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.FLAKE_NODE_SEQUENCE));


    public static final Integer JEDIS_PORT = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.JEDIS_PORT));
    public static final String JEDIS_HOST = FlakeConfig.configHolder().getConfigValue(FlakeConfig.JEDIS_HOST);


    public static final Integer N_THREADS = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.N_THREADS));
    public static final Integer BUFFER_SIZE = Integer.valueOf(FlakeConfig.configHolder().getConfigValue(
            FlakeConfig.BUFFER_SIZE));


    public static final String FLAKE_REDIS_QUEUE = "flake.redis.queue";
    public static final String REDIS_QUEUE_TYPE = "redis";
    public static final String KAFKA_QUEUE_TYPE = "kafka";

    public static final String QUEUE_TYPE = FlakeConfig.configHolder().getConfigValue(FlakeConfig.QUEUE_TYPE);
}
