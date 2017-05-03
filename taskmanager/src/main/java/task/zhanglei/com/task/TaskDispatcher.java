package task.zhanglei.com.task;

import java.util.concurrent.BlockingQueue;

/**
 * Created by zhanglei59 on 2017/4/21.
 */

public class TaskDispatcher extends Thread {

    private final BlockingQueue<Action<?>> mTaskQueue;

    private Action<?> mFuture;

    private volatile boolean mQuit = false;

    public TaskDispatcher(BlockingQueue<Action<?>> taskQueue) {
        this.mTaskQueue = taskQueue;
    }

    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (mFuture == null) {
                    scheduleNext();
                }
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }
        }
    }

    private void scheduleNext() throws InterruptedException {
        if ((mFuture = mTaskQueue.take()) != null) {
            try {
                mFuture.run();
            } finally {
                scheduleNext();
            }
        }
    }

}
