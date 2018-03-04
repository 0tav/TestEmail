package com.example.ireos.testemail.data;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.sent.domain.model.Mail;

import java.util.List;

/**
 * Created by tav on 01/03/2018.
 */

public interface MailDataSource {

    interface LoadMailCallback{

        void onMailLoaded(List<Mail> mail);

        void onDataNotAvailable();
    }

    interface GetMailCallback {

        void onMailLoaded(Mail mail);

        void onDataNotAvailable();
    }

    void getMail(@NonNull LoadMailCallback callback);

    void getMail(@NonNull String mailId, @NonNull GetMailCallback callback);

    void sentMail(@NonNull Mail mail);

    void favoriteMail(@NonNull Mail mail);

    void favoriteMail(@NonNull String mailId);

    void activateMail(@NonNull Mail mail);

    void activateMail(@NonNull String mailId);

    void deleteActiveMail();

    void refreshMail();

    void deleteAllMail();

    void deleteMail(@NonNull String mailId);

}
