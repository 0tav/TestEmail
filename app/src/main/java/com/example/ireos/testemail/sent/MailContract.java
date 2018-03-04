package com.example.ireos.testemail.sent;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ireos.testemail.base.BasePresenter;
import com.example.ireos.testemail.base.BaseView;
import com.example.ireos.testemail.sent.domain.model.Mail;

import java.util.List;

/**
 * Created by tav on 01/03/2018.
 */

public interface MailContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicatior(boolean active);

        void showMail(List<Mail> mail);

        void showComposeMail();

        void showEditMail(String mailId);

        void showMailMarkedFavorite();

        void showMailMarkedActive();

        void showActiveMailCleared();

        void showLoadingMailError();

        void showNoMail();

        void showActiveFilterLabel();

        void showFavoriteFilterLabel();

        void showAllFilterLabel();

        void showNoActiveMail();

        void showNoFavoriteMail();

        void showSuccessfullySentMail();

        void showMailDeleted();

        void showAlertSent();

        void showAlertDelete(@NonNull Mail requestedMail);

        void showAlertDeleteAll();

        boolean isFavorite();

        void showFilteringPopUpMenu();

    }

    interface Presenter extends BasePresenter{

        void result(int requestCode, int resultCode);

        void loadMail(boolean forceUpdate);

        void openEditMail(@NonNull Mail requestedMail);

        void addNewMail();

        void deleteMail(@NonNull Mail requestedMail);

        void favoriteMail(@NonNull Mail favoriteMail);

        void activeMail(@NonNull Mail activeMail);

        void deleteActiveMail();

        void setFiltering(MailFilterType requestType);

        MailFilterType getFiltering();
    }
}
