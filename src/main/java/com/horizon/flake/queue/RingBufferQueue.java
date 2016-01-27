package com.horizon.flake.queue;

import com.lmax.disruptor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author : David.Song/Java Engineer
 * @date : 2016/1/25 15:43
 * @see
 * @since : 1.0.0
 */
public class RingBufferQueue<T> {

    private static final Logger logger = LoggerFactory.getLogger(RingBufferQueue.class);
    private int BUFFER_SIZE = 1024;
    private int THREAD_NUM = 4;
    private RingBuffer<BufferEvent<T>> ringBuffer;
    private ExecutorService executor;
    private WorkerPool<BufferEvent<T>> workerPool;
    private WorkHandler<BufferEvent<T>> workHandler;

    public RingBufferQueue(EventWorkHandler workHandler, int nThreads, int bufferSize) {
        this.BUFFER_SIZE = bufferSize;
        this.THREAD_NUM = nThreads;
        this.workHandler = workHandler;
        this.check();
        this.ringBuffer = RingBuffer.createSingleProducer(new BufferEventFactory<T>(), BUFFER_SIZE);
        this.workerPool = new WorkerPool<>(ringBuffer, ringBuffer.newBarrier(),
                new IgnoreExceptionHandler(), this.workHandler);
        this.executor = Executors.newFixedThreadPool(THREAD_NUM);
        this.workerPool.start(this.executor);
        logger.info("RingBufferQueue threads:{},buffer_size:{}", THREAD_NUM, BUFFER_SIZE);
    }

    private void check() {
        int i = 1;
        while (i > 0) {
            if (i == BUFFER_SIZE)
                return;
            i <<= 1;
        }
        throw new RuntimeException("bufferSize must be a power of 2");
    }

    public void publish(T message) {
        ringBuffer.publishEvent(TRANSLATOR, message);
    }

    private final EventTranslatorOneArg<BufferEvent<T>, T> TRANSLATOR =
            new EventTranslatorOneArg<BufferEvent<T>, T>() {
                @Override
                public void translateTo(BufferEvent<T> event, long sequence, T message) {
                    event.setValue(message);
                }
            };

    public void shutdown() {
        workerPool.halt();
        executor.shutdown();
    }
}
