package task.zhanglei.com.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by zhanglei59 on 2017/4/21.
 */

public class TaskManager {

    private final BlockingQueue<Action<?>> messageQueue;

    private TaskDispatcher mTaskDispatcher;


    public TaskManager() {
        messageQueue = new PriorityBlockingQueue<>();

        mTaskDispatcher = new TaskDispatcher(messageQueue);
        mTaskDispatcher.start();
    }

    public Action<?> add(Action<?> task) {
        messageQueue.add(task);
        return task;
    }

    public void quitTaskQueue() {
        mTaskDispatcher.quit();
    }


}
