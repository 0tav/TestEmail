package com.example.ireos.testemail.data;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.sent.domain.model.Mail;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by tav on 02/03/2018.
 */

public class FakeMailRemoteDataResource implements MailDataSource{

    private static FakeMailRemoteDataResource INSTANCE;

    private static final Map<String, Mail> MAIL_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation
    private FakeMailRemoteDataResource(){}

    public static FakeMailRemoteDataResource getInstace(){
        if (INSTANCE == null){
            INSTANCE = new FakeMailRemoteDataResource();
        }
        return INSTANCE;
    }

    @Override
    public void getMail(@NonNull LoadMailCallback callback) {
        callback.onMailLoaded(Lists.newArrayList(MAIL_SERVICE_DATA.values()));
    }

    @Override
    public void getMail(@NonNull String mailId, @NonNull GetMailCallback callback) {
        Mail mail = MAIL_SERVICE_DATA.get(mailId);
        callback.onMailLoaded(mail);
    }

    @Override
    public void sentMail(@NonNull Mail mail) {
        MAIL_SERVICE_DATA.put(mail.getId(), mail);
    }

    @Override
    public void favoriteMail(@NonNull Mail mail) {
        Mail readMail = new Mail(mail.getTitle(), mail.getSubject(), mail.getDescription(), mail.getId(),true);
        MAIL_SERVICE_DATA.put(mail.getId(), readMail);
    }

    @Override
    public void favoriteMail(@NonNull String mailId) {
        //Not required for the remote data source.
    }

    @Override
    public void activateMail(@NonNull Mail mail) {
        Mail unreadMail = new Mail(mail.getTitle(), mail.getSubject(), mail.getDescription(), mail.getId());
        MAIL_SERVICE_DATA.put(mail.getId(), unreadMail);
    }

    @Override
    public void activateMail(@NonNull String mailId) {
        //Not required for the remote data source.
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
        // tasks from all the available data sources.
    }

    @Override
    public void deleteMail(@NonNull String mailId) {
        MAIL_SERVICE_DATA.remove(mailId);
    }

    @Override
    public void deleteAllMail() {
        MAIL_SERVICE_DATA.clear();
    }

}
