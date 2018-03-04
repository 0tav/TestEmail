package com.example.ireos.testemail.senteditmail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ireos.testemail.sent.domain.model.Mail;
import com.example.ireos.testemail.senteditmail.domain.GetMail;
import com.example.ireos.testemail.senteditmail.domain.SentMail;
import com.example.ireos.testemail.usecase.UseCase;
import com.example.ireos.testemail.usecase.UseCaseHandler;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 28/02/2018.
 */

public class SentEditPresenter implements SentEditContract.Presenter{

    private final SentEditContract.View mSentView;

    private final GetMail mGetMail;

    private final SentMail mSentMail;

    private final UseCaseHandler mUseCaseHandler;

    @Nullable
    private String mMailId;

    private boolean mIsDataMissing;

    public SentEditPresenter(@NonNull UseCaseHandler useCaseHandler, @Nullable String mailId,
                             @NonNull SentEditContract.View sentView, @NonNull GetMail getMail,
                             @NonNull SentMail sentMail, boolean shouldLoadDataFromRepo){
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mMailId = mailId;
        mSentView = checkNotNull(sentView, "sentView cannot be null!");
        mGetMail = checkNotNull(getMail, "getMail cannot be null!");
        mSentMail = checkNotNull(sentMail, "sentMail cannot be null!");
        mIsDataMissing = shouldLoadDataFromRepo;

        mSentView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewMail() && mIsDataMissing){
            populateMail();
        }
    }

    @Override
    public void sentEmail(String title, String subject, String description) {
        if (isNewMail()){
            createMail(title, subject, description);
        } else {
            updateMail(title, subject, description);
        }
    }

    @Override
    public void populateMail() {
        if (isNewMail()){
            throw new RuntimeException("populatedMail() was called but mail is new.");
        }

        mUseCaseHandler.execute(mGetMail, new GetMail.RequestValues(mMailId),
                new UseCase.UseCaseCallback<GetMail.ResponseValue>() {
                    @Override
                    public void onSuccess(GetMail.ResponseValue response) {
                        showMail(response.getMail());
                    }

                    @Override
                    public void onError() {
                        showEmptyMailError();
                    }
                });
    }

    private void showMail(Mail mail){
        // The view may not be able to handle UI updates anymore
        if (mSentView.isActive()){
            mSentView.setTitle(mail.getTitle());
            mSentView.setSubject(mail.getSubject());
            mSentView.setDescription(mail.getDescription());
        }

        mIsDataMissing = false;
    }

    private void showSentError(){
        //Show eror, log, etc.
    }

    private void showEmptyMailError(){
        // The view may not be able to handle UI updates anymore
        if (mSentView.isActive()){
            mSentView.showEmptyMailError();
        }
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewMail(){
        return mMailId == null;
    }

    private void createMail(String title, String subject, String description){
        Mail newMail = new Mail(title, subject, description);
        if (newMail.isEmpty()){
            mSentView.showEmptyMailError();
        } else {
            mUseCaseHandler.execute(mSentMail, new SentMail.RequestValues(newMail),
                    new UseCase.UseCaseCallback<SentMail.ResponseValue>() {
                        @Override
                        public void onSuccess(SentMail.ResponseValue response) {
                            mSentView.showMailList();
                        }

                        @Override
                        public void onError() {
                            showSentError();
                        }
                    });
        }
    }

    private void updateMail(String title, String subject, String description){
        if (isNewMail()){
            throw new RuntimeException("updateMail() was called but mail is new.");
        }
        Mail newMail = new Mail(title, subject, description, mMailId);
        if (newMail.isEmpty()){
            mSentView.showEmptyMailError();
        } else{
            mUseCaseHandler.execute(mSentMail, new SentMail.RequestValues(newMail),
                    new UseCase.UseCaseCallback<SentMail.ResponseValue>() {
                        @Override
                        public void onSuccess(SentMail.ResponseValue response) {
                            mSentView.showMailList();
                        }

                        @Override
                        public void onError() {
                            showSentError();
                        }
                    });
        }
    }
}
