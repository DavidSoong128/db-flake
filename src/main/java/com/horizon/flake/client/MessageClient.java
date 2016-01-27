package com.horizon.flake.client;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 17:05
 * @see
 * @since : 1.0.0
 */
public interface MessageClient {
    /**
     * 从队列中读取数据，开始处理
     */
    public void startProcess();

    public void stopProcess();
}
