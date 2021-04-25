package com.test.words.exec;

import java.util.concurrent.Executor;

/**
 * Simple {@link Executor implementation}. Creates new thread for each submitted task.
 */
public class RawThreadExecutor implements Executor {

    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }

}
