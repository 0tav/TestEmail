package com.example.ireos.testemail.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.ireos.testemail.sent.domain.model.Mail;

/**
 * Created by tav on 01/03/2018.
 */

@Database(entities = {Mail.class}, version = 1)

public abstract class MailDatabase extends RoomDatabase{

    private static MailDatabase INSTANCE;

    public abstract MailDao mailDao();

    private static final Object sLock = new Object();

    public static MailDatabase getInstance(Context context){
        synchronized (sLock){
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MailDatabase.class, "Mail.db")
                        .build();
            }
            return INSTANCE;
        }
    }
}
