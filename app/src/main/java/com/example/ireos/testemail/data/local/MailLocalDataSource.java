package com.example.ireos.testemail.data.local;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailDataSource;
import com.example.ireos.testemail.sent.domain.model.Mail;
import com.example.ireos.testemail.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 01/03/2018.
 */

public class MailLocalDataSource implements MailDataSource {

    private static volatile MailLocalDataSource INSTANCE;

    private MailDao mMailDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private MailLocalDataSource(@NonNull AppExecutors appExecutors,
                                @NonNull MailDao mailDao){
        mAppExecutors = appExecutors;
        mMailDao = mailDao;
    }

    public static MailLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                  @NonNull MailDao mailDao){
        if (INSTANCE == null){
            synchronized (MailLocalDataSource.class){
                if (INSTANCE == null){
                    INSTANCE = new MailLocalDataSource(appExecutors,mailDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getMail(@NonNull final LoadMailCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Mail> mail = mMailDao.getMail();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mail.isEmpty()){
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onMailLoaded(mail);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getMail(@NonNull final String mailId, @NonNull final GetMailCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Mail mail = mMailDao.getMailById(mailId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mail != null){
                            callback.onMailLoaded(mail);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void sentMail(@NonNull final Mail mail) {
        checkNotNull(mail);
        Runnable sentRunnable = new Runnable() {
            @Override
            public void run() {
                mMailDao.insertMail(mail);
            }
        };
        mAppExecutors.diskIO().execute(sentRunnable);
    }

    @Override
    public void favoriteMail(@NonNull final Mail mail) {
        Runnable completeRunnable = new Runnable() {
            @Override
            public void run() {
                mMailDao.updateFavorite(mail.getId(), true);
            }
        };
        mAppExecutors.diskIO().execute(completeRunnable);
    }

    @Override
    public void favoriteMail(@NonNull String mailId) {
        // Not required for the local data source because the {@link MailRepository} handles
        // converting from a {@code mailId} to a {@link mail} using its cached data.
    }

    @Override
    public void activateMail(@NonNull final Mail mail) {
        Runnable activateRunnable = new Runnable() {
            @Override
            public void run() {
                mMailDao.updateFavorite(mail.getId(), true);
            }
        };
        mAppExecutors.diskIO().execute(activateRunnable);
    }

    @Override
    public void activateMail(@NonNull String mailId) {
        // Not required for the local data source because the {@link MailRepository} handles
        // converting from a {@code mailId} to a {@link mail} using its cached data.
    }

    @Override
    public void deleteActiveMail() {
        Runnable deleteMailRunnable = new Runnable() {
            @Override
            public void run() {
                mMailDao.deleteActiveMail();
            }
        };
        mAppExecutors.diskIO().execute(deleteMailRunnable);
    }

    @Override
    public void refreshMail() {
        // Not required because the {@link MailRepository} handles the logic of refreshing the
        // mail from all the available data sources.
    }

    @Override
    public void deleteAllMail() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mMailDao.deleteMail();
            }
        };
        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteMail(@NonNull final String mailId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mMailDao.deleteMailById(mailId);
            }
        };
        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
