package com.example.ireos.testemail.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ireos.testemail.sent.domain.model.Mail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 01/03/2018.
 */

public class MailRepository implements MailDataSource {

    private static MailRepository INSTANCE = null;

    private final MailDataSource mMailRemoteDataSource;

    private final MailDataSource mMailLocalDataSource;

    Map<String, Mail> mCachedMail;

    boolean mCacheIsDirty = false;

    private MailRepository(@NonNull MailDataSource mailRemoteDataSource,
                           @NonNull MailDataSource mailLocalDataSource){
        mMailRemoteDataSource = checkNotNull(mailRemoteDataSource);
        mMailLocalDataSource = checkNotNull(mailLocalDataSource);
    }

    public static MailRepository getInstance(MailDataSource mailRemoteDataSource,
                                             MailDataSource mailLocalDataSource){
        if (INSTANCE == null){
            INSTANCE = new MailRepository(mailRemoteDataSource, mailLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){ INSTANCE = null;}

    public void getMail(@NonNull final LoadMailCallback callback){
        checkNotNull(callback);

        //Response immediately with cache if available and not dirty
        if (mCachedMail != null && !mCacheIsDirty){
            callback.onMailLoaded(new ArrayList<>(mCachedMail.values()));
            return;
        }

        if (mCacheIsDirty){
            // If the cache is dirty we need to fetch new data from the network.
            getMailFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. if not, query the network.
            mMailLocalDataSource.getMail(new LoadMailCallback() {
                @Override
                public void onMailLoaded(List<Mail> mail) {
                    refreshCache(mail);
                    callback.onMailLoaded(new ArrayList<>(mCachedMail.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getMailFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void sentMail(@NonNull Mail mail) {
        checkNotNull(mail);
        mMailRemoteDataSource.sentMail(mail);
        mMailLocalDataSource.sentMail(mail);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedMail == null){
            mCachedMail = new LinkedHashMap<>();
        }
        mCachedMail.put(mail.getId(), mail);
    }

    @Override
    public void favoriteMail(@NonNull Mail mail) {
        checkNotNull(mail);
        mMailRemoteDataSource.favoriteMail(mail);
        mMailLocalDataSource.favoriteMail(mail);

        Mail completedMail = new Mail(mail.getTitle(), mail.getSubject(),
                mail.getDescription(), mail.getId(), true);

        // Do in maemory cache update to keep the app UI up to date
        if (mCachedMail == null){
            mCachedMail = new LinkedHashMap<>();
        }
        mCachedMail.put(mail.getId(), completedMail);
    }

    @Override
    public void favoriteMail(@NonNull String mailId) {
        checkNotNull(mailId);
        favoriteMail(getMailWithId(mailId));
    }

    @Override
    public void activateMail(@NonNull Mail mail) {
        checkNotNull(mail);
        mMailRemoteDataSource.favoriteMail(mail);
        mMailLocalDataSource.favoriteMail(mail);

        Mail activeMail = new Mail(mail.getTitle(), mail.getSubject(),
                mail.getDescription(), mail.getId());

        // Do in maemory cache update to keep the app UI up to date
        if (mCachedMail == null){
            mCachedMail = new LinkedHashMap<>();
        }
        mCachedMail.put(mail.getId(), activeMail);
    }

    @Override
    public void activateMail(@NonNull String mailId) {
        checkNotNull(mailId);
        activateMail(getMailWithId(mailId));
    }

    @Override
    public void deleteActiveMail() {
        mMailRemoteDataSource.deleteActiveMail();
        mMailLocalDataSource.deleteActiveMail();

        // Do in memory cache update to keep the app UI up to date
        if (mCachedMail == null){
            mCachedMail = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Mail>> it = mCachedMail.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Mail> entry = it.next();
            if (entry.getValue().isActive()){
                it.remove();
            }
        }
    }

    @Override
    public void getMail(@NonNull final String mailId, @NonNull final GetMailCallback callback) {
        checkNotNull(mailId);
        checkNotNull(callback);

        Mail cachedMail = getMailWithId(mailId);

        //Respond immediately with cache if available
        if (cachedMail != null){
            callback.onMailLoaded(cachedMail);
            return;
        }

        //Load from server/persited if needed.

        //Is the task in the local data source? If not, query the network.
        mMailLocalDataSource.getMail(mailId, new GetMailCallback() {
            @Override
            public void onMailLoaded(Mail mail) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedMail == null){
                    mCachedMail = new LinkedHashMap<>();
                }
                mCachedMail.put(mailId, mail);
                callback.onMailLoaded(mail);
            }

            @Override
            public void onDataNotAvailable() {
                mMailRemoteDataSource.getMail(mailId, new GetMailCallback() {
                    @Override
                    public void onMailLoaded(Mail mail) {
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedMail == null){
                            mCachedMail = new LinkedHashMap<>();
                        }
                        mCachedMail.put(mailId, mail);
                        callback.onMailLoaded(mail);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void refreshMail() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllMail() {
        mMailRemoteDataSource.deleteAllMail();
        mMailLocalDataSource.deleteAllMail();

        if (mCachedMail == null){
            mCachedMail = new LinkedHashMap<>();
        }
        mCachedMail.clear();
    }

    @Override
    public void deleteMail(@NonNull String mailId) {
        mMailRemoteDataSource.deleteMail(checkNotNull(mailId));
        mMailLocalDataSource.deleteMail(checkNotNull(mailId));

        mCachedMail.remove(mailId);
    }

    private void getMailFromRemoteDataSource(@NonNull final LoadMailCallback callback){
        mMailRemoteDataSource.getMail(new LoadMailCallback() {
            @Override
            public void onMailLoaded(List<Mail> mail) {
                refreshCache(mail);
                refreshLocalDataSource(mail);
                callback.onMailLoaded(new ArrayList<>(mCachedMail.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Mail> mail){
        if (mCachedMail == null){
            mCachedMail = new LinkedHashMap<>();
        }
        mCachedMail.clear();
        for (Mail m : mail){
            mCachedMail.put(m.getId(), m);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Mail> mail){
        mMailLocalDataSource.deleteAllMail();
        for (Mail m : mail){
            mMailLocalDataSource.sentMail(m);
        }
    }

    @Nullable
    private Mail getMailWithId(@NonNull String id){
        checkNotNull(id);
        if (mCachedMail == null || mCachedMail.isEmpty()){
            return null;
        } else {
            return mCachedMail.get(id);
        }
    }
}
