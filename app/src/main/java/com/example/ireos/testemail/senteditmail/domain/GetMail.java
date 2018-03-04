package com.example.ireos.testemail.senteditmail.domain;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailDataSource;
import com.example.ireos.testemail.data.MailRepository;
import com.example.ireos.testemail.sent.domain.model.Mail;
import com.example.ireos.testemail.usecase.UseCase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 28/02/2018.
 */

public class GetMail extends UseCase<GetMail.RequestValues, GetMail.ResponseValue>{

    private final MailRepository mMailRepository;

    public GetMail(@NonNull MailRepository mailRepository){
        mMailRepository = checkNotNull(mailRepository, "mailRepository cannot be null");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        mMailRepository.getMail(values.getMailId(), new MailDataSource.GetMailCallback() {
            @Override
            public void onMailLoaded(Mail mail) {
                if (mail != null){
                    ResponseValue responseValue = new ResponseValue(mail);
                    getUseCaseCallback().onSuccess(responseValue);
                } else {
                    getUseCaseCallback().onError();
                }
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues{

        private final String mMailId;

        public RequestValues(@NonNull String mailId){
            mMailId = checkNotNull(mailId, "mailId cannot be null");
        }

        public String getMailId(){
            return mMailId;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue{

        private Mail mMail;

        public ResponseValue(@NonNull Mail mail){
            mMail = checkNotNull(mail, "mail cannot be null");
        }

        public Mail getMail(){
            return mMail;
        }
    }
}
