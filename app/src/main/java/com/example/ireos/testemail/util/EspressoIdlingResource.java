package com.example.ireos.testemail.util;

import android.support.test.espresso.IdlingResource;

/**
 * Created by tav on 01/03/2018.
 */

public class EspressoIdlingResource {

    private static final String RESOURCE = "GLOBAL";

    private static final SimpleCountingIdlingResource DEFAULT_INSTANCE =
            new SimpleCountingIdlingResource(RESOURCE);

    public static void increment(){
        DEFAULT_INSTANCE.increment();
    }

    public static void decrement(){
        DEFAULT_INSTANCE.decrement();
    }

    public static IdlingResource getIdlingResource(){
        return DEFAULT_INSTANCE;
    }
}
