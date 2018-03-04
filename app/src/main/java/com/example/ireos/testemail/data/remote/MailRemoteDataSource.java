package com.example.ireos.testemail.data.remote;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailDataSource;
import com.example.ireos.testemail.sent.domain.model.Mail;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by tav on 01/03/2018.
 */

public class MailRemoteDataSource implements MailDataSource{

    private static MailRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILIS = 500;

    private static final Map<String, Mail> MAIL_SERVICE_DATA;

    static {
        MAIL_SERVICE_DATA = new LinkedHashMap<>(2);
        sentMail("nyaa@gmail.com","Build tower in Pisa", "Ground looks good.");
        sentMail("rao@gmail.com","Meal","hunggry");
    }

    public static MailRemoteDataSource getInstance(){
        if (INSTANCE == null){
            INSTANCE = new MailRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantion.
    private MailRemoteDataSource(){ }

    private static void sentMail(String title, String subject, String description){
        Mail newMail = new Mail(title, subject, description);
        MAIL_SERVICE_DATA.put(newMail.getId(), newMail);
    }

    @Override
    public void getMail(@NonNull final LoadMailCallback callback) {
        // Simulate network by delaying the execution.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onMailLoaded(Lists.newArrayList(MAIL_SERVICE_DATA.values()));
            }
        },SERVICE_LATENCY_IN_MILIS);
    }

    @Override
    public void getMail(@NonNull String mailId, final @NonNull GetMailCallback callback) {
        final Mail mail = MAIL_SERVICE_DATA.get(mailId);
        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onMailLoaded(mail);
            }
        },SERVICE_LATENCY_IN_MILIS);
    }

    @Override
    public void sentMail(@NonNull Mail mail) {
        MAIL_SERVICE_DATA.put(mail.getId(), mail);
    }

    @Override
    public void favoriteMail(@NonNull Mail mail) {
        Mail readMail = new Mail(mail.getTitle(), mail.getSubject(), mail.getDescription(),
                mail.getId(), true);
        MAIL_SERVICE_DATA.put(mail.getId(), readMail);
    }

    @Override
    public void favoriteMail(@NonNull String mailId) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void activateMail(@NonNull Mail mail) {
        Mail activeMail = new Mail(mail.getTitle(), mail.getSubject(), mail.getDescription(),
                mail.getId());
        MAIL_SERVICE_DATA.put(mail.getId(), activeMail);
    }

    @Override
    public void activateMail(@NonNull String mailId) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void deleteActiveMail() {
        Iterator<Map.Entry<String, Mail>> it = MAIL_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Mail> entry = it.next();
            if (entry.getValue().isActive()){
                it.remove();
            }
        }
    }

    @Override
    public void refreshMail() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // mail from all the available data source.
    }

    @Override
    public void deleteAllMail() {
        MAIL_SERVICE_DATA.clear();
    }

    @Override
    public void deleteMail(@NonNull String mailId) {
        MAIL_SERVICE_DATA.remove(mailId);
    }
}
