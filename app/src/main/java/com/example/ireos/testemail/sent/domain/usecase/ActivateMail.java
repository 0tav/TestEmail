package com.example.ireos.testemail.sent.domain.usecase;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailRepository;
import com.example.ireos.testemail.usecase.UseCase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 01/03/2018.
 */

public class ActivateMail extends UseCase<ActivateMail.RequestValues, ActivateMail.ResponseValue> {

    private final MailRepository mMailRepository;

    public ActivateMail(@NonNull MailRepository mailRepository){
        mMailRepository = checkNotNull(mailRepository, "mailRepository cannot be null");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        String activeMail = values.getActivateMail();
        mMailRepository.activateMail(activeMail);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues{

        private final String mActivateMail;

        public RequestValues(@NonNull String activateMail){
            mActivateMail = checkNotNull(activateMail, "activateMail cannot be null");
        }

        public String getActivateMail(){ return mActivateMail; }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{ }
}
