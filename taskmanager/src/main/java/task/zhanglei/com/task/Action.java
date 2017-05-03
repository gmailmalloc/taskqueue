package task.zhanglei.com.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


/**
 * Created by baidu on 2017/4/25.
 */

public abstract class Action<ResultT> implements Callable<ResultT>, Comparable<Action<?>> {

    private static final int MESSAGE_POST_START = 0x1;
    private static final int MESSAGE_POST_RESULT = 0x2;

    private final boolean mIsBackgroundTask;

    private Priority mPriotity;


    public enum Priority {

        BACKGROUND(0),
        UI(1);

        private int priotity;

        Priority(int priotity) {
            this.priotity = priotity;
        }

        public int getPriotity() {
            return priotity;
        }
    }

    private Priority getPriority() {
        return mPriotity;
    }


    protected void onStart() {

    }

    protected void onFinish(ResultT result) {

    }

    protected void onError() {

    }

    protected abstract ResultT doBackground();


    private FutureTask<ResultT> mFuture;

    private static Handler mHandler;

    public Action(boolean isBackgroundTask) {
        this.mIsBackgroundTask = isBackgroundTask;
        if (isBackgroundTask) {
            mPriotity = Priority.BACKGROUND;
        } else {
            mPriotity = Priority.UI;
        }
        mFuture = new FutureTask<ResultT>(this) {

            @Override
            protected void done() {
                try {
                    postResult(get(), mIsBackgroundTask);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occurred while executing doInBackground()",
                            e.getCause());
                }
            }
        };
    }


    @Override
    public ResultT call() {
        ResultT result = null;
        try {
            result = doBackground();
        } catch (Exception e) {
            throw e;
        } finally {
            postResult(result, mIsBackgroundTask);
        }

        return result;
    }

    public void run() {
        postStart(mIsBackgroundTask);
        mFuture.run();
    }

    private static Handler getHandler() {
        if (mHandler == null) {
            mHandler = new InternalHandler();
        }
        return mHandler;
    }

    private void postStart(boolean isBackgroundTask) {
        if (isBackgroundTask) {
            onStart();
        } else {
            Message message = getHandler().obtainMessage(MESSAGE_POST_START,
                    new ActionResult(this, null));
            message.sendToTarget();
        }
    }


    private void postResult(ResultT result, boolean isBackgroundTask) {
        if (isBackgroundTask) {
            onFinish(result);
        } else {
            Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                    new ActionResult(this, result));
            message.sendToTarget();

        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            ActionResult<?> result = (ActionResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    result.mTask.onFinish(result.mData);
                    break;
                case MESSAGE_POST_START:
                    result.mTask.onStart();
                    break;
                default:
                    break;
            }
        }
    }

    private static class ActionResult<ResultT> {
        final Action mTask;
        final ResultT mData;

        ActionResult(Action task, ResultT data) {
            mTask = task;
            mData = data;
        }
    }

    /**
     * 做任务优先级排序
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Action<?> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();
        return left.getPriotity() - right.getPriotity();
    }

}
