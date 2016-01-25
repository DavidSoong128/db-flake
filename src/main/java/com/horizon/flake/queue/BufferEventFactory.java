package com.horizon.flake.queue;

import com.lmax.disruptor.EventFactory;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 16:19
 * @see
 * @since : 1.0.0
 */
public class BufferEventFactory<T> implements EventFactory<BufferEvent<T>>{
    @Override
    public BufferEvent<T> newInstance() {
        return new BufferEvent<>();
    }
}
