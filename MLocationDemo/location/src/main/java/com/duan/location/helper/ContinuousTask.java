package com.duan.location.helper;

import android.os.Handler;

import androidx.annotation.NonNull;

/** Task -- TaskSchedule */
public class ContinuousTask extends Handler implements Runnable{

    private final String taskId;
    private final ContinuousTaskScheduler continuousTaskScheduler;  // 调度器
    private final ContinuousTaskRunner continuousTaskRunner;

    public ContinuousTask(@NonNull String taskId, @NonNull ContinuousTaskRunner continuousTaskRunner) {
        this.taskId = taskId;
        continuousTaskScheduler = new ContinuousTaskScheduler(this);
        this.continuousTaskRunner = continuousTaskRunner;
    }

    public void delayed(long delay) {
        continuousTaskScheduler.delayed(delay);
    }

    public void pause() {
        continuousTaskScheduler.onPause();
    }

    public void resume() {
        continuousTaskScheduler.onResume();
    }

    public void stop() {
        continuousTaskScheduler.onStop();
    }

    // 当前时间
    long getCurrentTime() {
        return System.currentTimeMillis();
    }



    // follow implements Runnable /////////////////////////
    @Override
    public void run() {
        continuousTaskRunner.runScheduledTask(taskId);
    }

    void schedule(long delay) {
        postDelayed(this, delay); // extends Handler
    }

    void unregister() {
        removeCallbacks(this);  // extends Handler
    }


    public interface ContinuousTaskRunner {
        /**
         * Callback to take action when scheduled time is arrived.
         * Called with given taskId in order to distinguish which task should be run,
         * in case of same {@linkplain ContinuousTaskRunner} passed to multiple Tasks
         */
        void runScheduledTask(@NonNull String taskId);
    }
}
