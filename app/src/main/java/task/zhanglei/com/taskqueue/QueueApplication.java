package task.zhanglei.com.taskqueue;

import android.app.Application;

import task.zhanglei.com.task.TaskManager;

/**
 * Created by baidu on 2017/5/3.
 */

public class QueueApplication extends Application {

    private static TaskManager taskManager;

    public static TaskManager getTaskManager() {
        if (taskManager == null) {
            taskManager = new TaskManager();
        }
        return taskManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
