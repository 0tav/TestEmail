package com.example.ireos.testemail.senteditmail;

import com.example.ireos.testemail.base.BasePresenter;
import com.example.ireos.testemail.base.BaseView;

/**
 * Created by tav on 28/02/2018.
 */

public interface SentEditContract {

    interface View extends BaseView<Presenter> {

        void showEmptyMailError();

        void showMailList();

        void setTitle(String title);

        void setSubject(String subject);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void sentEmail(String title, String subject, String description);

        void populateMail();

        boolean isDataMissing();
    }
}
