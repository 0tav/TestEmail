package com.example.ireos.testemail.sent;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ireos.testemail.sent.domain.model.Mail;
import com.example.ireos.testemail.sent.domain.usecase.ActivateMail;
import com.example.ireos.testemail.sent.domain.usecase.DeleteActiveMail;
import com.example.ireos.testemail.sent.domain.usecase.FavoriteMail;
import com.example.ireos.testemail.sent.domain.usecase.GetMails;
import com.example.ireos.testemail.senteditmail.SentEditActivity;
import com.example.ireos.testemail.senteditmail.domain.DeleteMail;
import com.example.ireos.testemail.usecase.UseCase;
import com.example.ireos.testemail.usecase.UseCaseHandler;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 01/03/2018.
 */

public class MailPresenter implements MailContract.Presenter{

    private final MailContract.View mMailView;
    private final GetMails mGetMails;
    private final FavoriteMail mFavoriteMail;
    private final ActivateMail mActivateMail;
    private final DeleteMail mDeleteAllMail;
    private final DeleteActiveMail mDeleteActiveMail;

    private MailFilterType mCurrentFiltering = MailFilterType.ALL_MAIL;

    private boolean mFirstLoad = true;

    private final UseCaseHandler mUseCaseHandler;

    @Nullable
    private String mMailId;

    public MailPresenter(@NonNull UseCaseHandler useCaseHandler, @Nullable String mailId,
                         @NonNull MailContract.View mailView, @NonNull GetMails getMails,
                         @NonNull FavoriteMail favoriteMail, @NonNull ActivateMail activateMail,
                         @NonNull DeleteMail deleteMail, @NonNull DeleteActiveMail deleteActiveMail){
        mMailId = mailId;
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mMailView = checkNotNull(mailView, "mailView cannot be null!");
        mGetMails = checkNotNull(getMails, "getMails cannot be null!");
        mFavoriteMail = checkNotNull(favoriteMail, "favoriteMail cannot be null!");
        mActivateMail = checkNotNull(activateMail, "activateMail cannot be null!");
        mDeleteAllMail = checkNotNull(deleteMail, "deleteMail cannot be null!");
        mDeleteActiveMail = checkNotNull(deleteActiveMail, "deleteActiveMail cannot be null!");

        mMailView.setPresenter(this);
    }

    @Override
    public void start() {
        loadMail(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a mail was sucessfully added, show snackbar
        if (SentEditActivity.REQUEST_ADD_MAIL == requestCode
                && Activity.RESULT_OK == resultCode){
            mMailView.showSuccessfullySentMail();
        }
    }

    @Override
    public void loadMail(boolean forceUpdate) {
        //Simplifiacation for sample: a network reload will be forced on first load.
        loadMail(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void loadMail(boolean forceUpdate, final boolean showLoadingUI){
        if (showLoadingUI){
            mMailView.setLoadingIndicatior(true);
        }

        GetMails.RequestValues requestValues = new GetMails.RequestValues(forceUpdate,
                mCurrentFiltering);

        mUseCaseHandler.execute(mGetMails, requestValues,
                new UseCase.UseCaseCallback<GetMails.ResponseValue>() {
                    @Override
                    public void onSuccess(GetMails.ResponseValue response) {
                        List<Mail> mail = response.getMail();
                        // The view may not be ablne to handle UI updates anymore
                        if (!mMailView.isFavorite()){
                            return;
                        }
                        if (showLoadingUI){
                            mMailView.setLoadingIndicatior(false);
                        }

                        processMail(mail);
                    }

                    @Override
                    public void onError() {
                        // The view may not able to handle UI update anymore
                        if (!mMailView.isFavorite()){
                            return;
                        }
                        mMailView.showLoadingMailError();
                    }
                });
    }

    private void processMail(List<Mail> mail){
        if (mail.isEmpty()){
            // Show a message indicating there are no mail for that filter type.
            processEmptyMail();
        } else {
            // Show the list of mail
            mMailView.showMail(mail);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel(){
        switch (mCurrentFiltering){
            case ACTIVE_MAIL:
                mMailView.showActiveFilterLabel();
                break;
            case FAVORITE_MAIL:
                mMailView.showFavoriteFilterLabel();
                break;
            default:
                mMailView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyMail(){
        switch (mCurrentFiltering){
            case ACTIVE_MAIL:
                mMailView.showNoActiveMail();
                break;
            case FAVORITE_MAIL:
                mMailView.showNoFavoriteMail();
                break;
            default:
                mMailView.showNoMail();
                break;
        }
    }

    @Override
    public void openEditMail(@NonNull Mail requestedMail) {
        checkNotNull(requestedMail, "requestedMail cannot be null");
        mMailView.showEditMail(requestedMail.getId());
    }

    @Override
    public void addNewMail() {
        mMailView.showComposeMail();
    }

    @Override
    public void deleteMail(@NonNull final Mail requestedMail) {
        checkNotNull(requestedMail, "requestedMail cannot be null");
        String mMailId = requestedMail.getId();
        mUseCaseHandler.execute(mDeleteAllMail, new DeleteMail.RequestValues(mMailId),
                new UseCase.UseCaseCallback<DeleteMail.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteMail.ResponseValue response) {
                        mMailView.showMailDeleted();
                        loadMail(false,false);
                    }

                    @Override
                    public void onError() {
                        // Show error, log, etc.
                    }
                });
    }

    @Override
    public void favoriteMail(@NonNull Mail favoriteMail) {
        checkNotNull(favoriteMail,"readMail cannot be null");
        mUseCaseHandler.execute(mFavoriteMail, new FavoriteMail.RequestValues(favoriteMail.getId()),
                new UseCase.UseCaseCallback<FavoriteMail.ResponseValue>() {
                    @Override
                    public void onSuccess(FavoriteMail.ResponseValue response) {
                        mMailView.showMailMarkedFavorite();
                        loadMail(false, false);
                    }

                    @Override
                    public void onError() {
                        mMailView.showLoadingMailError();
                    }
                });
    }

    @Override
    public void activeMail(@NonNull Mail activeMail) {
        checkNotNull(activeMail,"unreadMail cannot be null");
        mUseCaseHandler.execute(mActivateMail, new ActivateMail.RequestValues(activeMail.getId()),
                new UseCase.UseCaseCallback<ActivateMail.ResponseValue>() {
                    @Override
                    public void onSuccess(ActivateMail.ResponseValue response) {
                        mMailView.showMailMarkedActive();
                        loadMail(false, false);
                    }

                    @Override
                    public void onError() {
                        mMailView.showLoadingMailError();
                    }
                });
    }

    @Override
    public void deleteActiveMail() {
        mUseCaseHandler.execute(mDeleteActiveMail, new DeleteActiveMail.RequestValues(),
                new UseCase.UseCaseCallback<DeleteActiveMail.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteActiveMail.ResponseValue response) {
                        mMailView.showActiveMailCleared();
                        loadMail(false,false);
                    }

                    @Override
                    public void onError() {
                        mMailView.showLoadingMailError();
                    }
                });
    }

    @Override
    public void setFiltering(MailFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public MailFilterType getFiltering() {
        return mCurrentFiltering;
    }
}
