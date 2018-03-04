package com.example.ireos.testemail.senteditmail.domain;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailRepository;
import com.example.ireos.testemail.usecase.UseCase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 28/02/2018.
 */

public class DeleteMail extends UseCase<DeleteMail.RequestValues, DeleteMail.ResponseValue> {

    private final MailRepository mMailRepository;

    public DeleteMail(@NonNull MailRepository mailRepository){
        mMailRepository = checkNotNull(mailRepository, "mailRepository cannot be null");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        mMailRepository.deleteMail(values.getMailId());
        getUseCaseCallback().onSuccess(new ResponseValue());
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

    public static final class ResponseValue implements UseCase.ResponseValue{}
}
