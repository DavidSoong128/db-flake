package com.horizon.flake.queue;

import com.horizon.flake.core.EventSqlHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 16:13
 * @see
 * @since : 1.0.0
 */
public class EventWorkHandler<T> implements WorkHandler<BufferEvent<T>>{

    private EventSqlHandler eventSqlHandler;

    public EventWorkHandler(EventSqlHandler handler){
        this.eventSqlHandler = handler;
    }
    @Override
    public void onEvent(BufferEvent<T> event) throws Exception {
        T t = event.getValue();
        eventSqlHandler.sqlHandler(t);
    }
}
