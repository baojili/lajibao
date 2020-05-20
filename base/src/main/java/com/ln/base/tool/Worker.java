package com.ln.base.tool;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import org.jetbrains.annotations.NotNull;

public class Worker {
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private String name;
    private volatile boolean isStarted = false;

    public Worker(String name) {
        this.name = name;
        initHandlerThread();
    }

    private void initHandlerThread() {
        mHandlerThread = new HandlerThread("name");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof Runnable) {
                    if (!work(((Runnable) msg.obj))) {
                        Log.e("task run failed.");
                    }
                }
            }
        };
        isStarted = true;
    }

    protected boolean work(@NotNull Runnable task) {
        if (!isStarted) {
            Log.w(name + "is not started or quited,task will be not executed!");
            return false;
        }
        if (task == null) return false;
        task.run();
        return true;
    }

    public boolean sendTask(@NotNull Runnable task) {
        if (!isStarted) {
            Log.w(name + "is not started or quited,task will be not executed!");
            return false;
        }
        if (task == null) return false;
        return mHandler.sendMessage(mHandler.obtainMessage(0, task));
    }

    public boolean sendTaskToFront(@NotNull Runnable task) {
        if (!isStarted) {
            Log.w(name + "is not started or quited,task will be not executed!");
            return false;
        }
        if (task == null) return false;
        return mHandler.sendMessageAtFrontOfQueue(mHandler.obtainMessage(0, task));
    }

    public boolean sendTaskDelayed(@NotNull Runnable task, long delayMillis) {
        if (!isStarted) {
            Log.w(name + "is not started or quited,task will be not executed!");
            return false;
        }
        if (task == null) return false;
        return mHandler.sendMessageDelayed(mHandler.obtainMessage(0, task), delayMillis);
    }

    public boolean removeCallbacks(@NotNull Runnable task) {
        if (!isStarted) {
            Log.w(name + "is not started or quited,task will be not executed!");
            return false;
        }
        if (task == null) return false;
        mHandler.removeCallbacksAndMessages(task);
        return true;
    }

    public void quit() {
        if (!isStarted) {
            Log.w(name + "is quited!");
        }
        mHandlerThread.interrupt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mHandlerThread.quitSafely();
        } else {
            mHandlerThread.quit();
        }
    }

    public Handler getHandler() {
        return mHandler;
    }

    public String getName() {
        return name;
    }

    public boolean isStarted() {
        return isStarted;
    }
}
