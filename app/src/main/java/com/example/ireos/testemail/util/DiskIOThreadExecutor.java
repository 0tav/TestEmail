package com.example.ireos.testemail.util;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by tav on 01/03/2018.
 */

public class DiskIOThreadExecutor implements Executor{

    private final Executor mDiskIO;

    public DiskIOThreadExecutor(){
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mDiskIO.execute(command);
    }
}
