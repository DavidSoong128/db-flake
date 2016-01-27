package com.horizon.flake.util;


import com.horizon.flake.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisSingleHandler {

    private Logger log = LoggerFactory.getLogger(RedisSingleHandler.class);

    private FstSerializer serializer = new FstSerializer();

    private JedisPool pool;

    private RedisSingleHandler() {
    }

    private static class RedisSingleHolder {
        private static RedisSingleHandler redisHolder = new RedisSingleHandler();
    }

    public static RedisSingleHandler redisHolder() {
        return RedisSingleHolder.redisHolder;
    }

    private Jedis getJedis() {
        if (pool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(100);
            config.setMaxTotal(500);
            config.setMaxWaitMillis(10000);
            pool = new JedisPool(config, Constants.JEDIS_HOST, Constants.JEDIS_PORT);
        }
        return pool.getResource();
    }

    public void returnResource(Jedis jedis) {
        if (null != jedis) {
            pool.returnResourceObject(jedis);
        }
    }

    public void lpushDBEvent(String key, Object event) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            byte[] keys = key.getBytes();
            byte[] eventByte = serializer.serialize(event);
            jedis.lpush(keys, eventByte);
        } catch (Exception e) {
            log.error("jedis lpushDBEvent error", e);
        } finally {
            this.returnResource(jedis);
        }
    }

    public long llen(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            byte[] keys = key.getBytes();
            return jedis.llen(keys);
        } catch (Exception e) {
            log.error("jedis llen error", e);
        } finally {
            this.returnResource(jedis);
        }
        return 0;
    }

    public Object rPopDBEvent(String key) {
        Jedis jedis = null;
        Object event = null;
        try {
            jedis = getJedis();
            byte[] keys = key.getBytes();
            byte[] data = jedis.rpop(keys);
            if (null != data) {
                event = serializer.deserialize(data);
            }
        } catch (Exception e) {
            log.error("jedis getDBEvent error", e);
        } finally {
            this.returnResource(jedis);
        }
        return event;
    }

}