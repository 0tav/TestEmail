package com.example.ireos.testemail.senteditmail.domain;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailRepository;
import com.example.ireos.testemail.sent.domain.model.Mail;
import com.example.ireos.testemail.usecase.UseCase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 28/02/2018.
 */

public class SentMail extends UseCase<SentMail.RequestValues, SentMail.ResponseValue>{

    private final MailRepository mMailRepository;

    public SentMail(@NonNull MailRepository mailRepository){
        mMailRepository = checkNotNull(mailRepository, "mailRepository cannot be null");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        Mail mail = values.getMail();
        mMailRepository.sentMail(mail);

        getUseCaseCallback().onSuccess(new ResponseValue(mail));
    }

    public static final class RequestValues implements UseCase.RequestValues{

        private final Mail mMail;

        public RequestValues(@NonNull Mail mail){
            mMail = checkNotNull(mail, "mail cannot be null");
        }

        public Mail getMail(){
            return mMail;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue{

        private final Mail mMail;

        public ResponseValue(@NonNull Mail mail){
            mMail = checkNotNull(mail, "mail cannot be null");
        }

        public Mail getMail() { return mMail;}
    }
}
