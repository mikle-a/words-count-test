package com.test.words.exec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

class RawThreadExecutorTest {

    @Test
    public void testExecute() throws InterruptedException, ExecutionException {
        //given rat thread executor
        final RawThreadExecutor executor = new RawThreadExecutor();

        //and task which will complete immediately after receiving first element from the sync queue
        final SynchronousQueue<Object> queue = new SynchronousQueue<>();
        final FutureTask<Object> task = new FutureTask<>(() -> {
            final Object poll = queue.poll(Long.MAX_VALUE, TimeUnit.SECONDS);
            System.out.println("poll");
            return poll;
        });

        //when task is submitted
        executor.execute(task);

        //then task is not complete
        Assertions.assertFalse(task.isDone());

        //when result is pushed to the queue
        final Object result = new Object();
        queue.offer(result, Long.MAX_VALUE, TimeUnit.SECONDS);

        //then task is complete
        Assertions.assertSame(result, task.get());
    }

}