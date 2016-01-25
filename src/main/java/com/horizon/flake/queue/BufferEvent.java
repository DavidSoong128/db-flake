package com.horizon.flake.queue;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 16:01
 * @see
 * @since : 1.0.0
 */
public class BufferEvent<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
