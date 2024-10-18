package com.example.roomdatabase.executors;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

public class AppExecutors {

    // Singleton pattern with double-checked locking
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    // Private constructor to initialize executors
    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    // Public method to get the single instance
    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new AppExecutors(
                            Executors.newSingleThreadExecutor(),  // Disk I/O operations
                            Executors.newFixedThreadPool(3),      // Network I/O operations
                            new MainThreadExecutor()              // Main thread operations
                    );
                }
            }
        }
        return sInstance;
    }

    // Getters for the executors
    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    // Inner class to handle main thread operations using a Handler
    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
