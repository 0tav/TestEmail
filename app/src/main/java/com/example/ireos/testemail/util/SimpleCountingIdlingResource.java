package com.example.ireos.testemail.util;

import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 01/03/2018.
 */

public class SimpleCountingIdlingResource implements IdlingResource {

    private final String mResourceName;

    private final AtomicInteger counter = new AtomicInteger(0);

    // written from main thread, read from any thread.
    private volatile ResourceCallback reasourceCallback;

    public SimpleCountingIdlingResource(@NonNull String resourceName){
        mResourceName = checkNotNull(resourceName);
    }

    @Override
    public String getName() {
        return mResourceName;
    }

    @Override
    public boolean isIdleNow() {
        return counter.get() == 0;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback reasourceCallback) {
        this.reasourceCallback = reasourceCallback;
    }

    public void increment(){
        counter.getAndIncrement();
    }

    public void decrement(){
        int counterVal = counter.decrementAndGet();
        if (counterVal == 0){
            // we've gone from non-zero to zero. that means we've idle now! Tell espresso.
            if (null != reasourceCallback){
                reasourceCallback.onTransitionToIdle();
            }
        }

        if (counterVal < 0){
            throw new IllegalArgumentException("Counter has been corrupted!");
        }
    }
}
