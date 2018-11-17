package br.com.fbs.popularmovies.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by felipe on 15/11/18.
 */

public class ThreadExecutor implements Executor {
    @Override
    public void execute(@NonNull Runnable command) {
        new Thread(command).start();
    }
}
