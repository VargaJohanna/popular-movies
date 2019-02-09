package com.movies.popularmoviesjava.utilities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor networkIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3));
            }
        }
        return sInstance;
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }


}
