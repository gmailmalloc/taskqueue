package task.zhanglei.com.taskqueue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import task.zhanglei.com.task.Action;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickedButton(View view) {
        QueueApplication.getTaskManager().add(new Action<Integer>(true) {
            @Override
            protected Integer doBackground() {
                return null;
            }
        });
    }

}
