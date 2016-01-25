package com.horizon.flake.common;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 11:27
 * @see
 * @since : 1.0.0
 */
public class FlakeConfig {

    private final static String CONFIG_PATH = "/db-flake.properties";

    public final static String JDBC_DRIVER = "jdbc.driver";
    public final static String JDBC_URL = "jdbc.url";
    public final static String JDBC_USERNAME = "jdbc.username";
    public final static String JDBC_PASSWORD = "jdbc.password";
    public final static String JDBC_POOL_MAXIDLE = "jdbc.pool.maxIdle";
    public final static String JDBC_POOL_MINIDLE = "jdbc.pool.minIdle";
    public final static String JDBC_POOL_INITSIZE = "jdbc.pool.initSize";
    public final static String JDBC_POOL_MAXACTIVE = "jdbc.pool.maxActive";
    public final static String FLAKE_THREAD_POOL_SIZE = "thread.pool.size";
    public final static String QUEUE_SLEEP_INTERVAL = "queue.sleep.interval";
    public final static String FLAKE_NODE_SEQUENCE = "node.sequence";

    public final static String JEDIS_PORT = "jedis.port";
    public final static String JEDIS_HOST = "jedis.host";

    public final static String N_THREADS = "nthreads";
    public final static String BUFFER_SIZE = "buffer.size";

    public final static String QUEUE_TYPE = "queue.type";

    private FlakeConfig() {
    }

    private static class Holder {
        private static FlakeConfig config	= new FlakeConfig();
    }

    public static FlakeConfig configHolder() {
        return Holder.config;
    }

    private Properties prop	= getProperties(CONFIG_PATH);

    private static Properties getProperties(String url) {
        try {
            Properties prop = new Properties();
            InputStream in = FlakeConfig.class.getResourceAsStream(url);
            prop.load(in);
            in.close();
            return prop;
        } catch (Exception e) {
        }
        return null;
    }

    public String getConfigValue(String configKey){
        String configValue = prop.getProperty(configKey);
        if(configValue == null || configValue.length() == 0)
            throw new IllegalArgumentException(configValue+" can`t be null");
        return configValue;
    }
}
