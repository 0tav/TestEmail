package com.example.ireos.testemail.sent.domain.usecase;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailRepository;
import com.example.ireos.testemail.usecase.UseCase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 01/03/2018.
 */

public class FavoriteMail extends UseCase<FavoriteMail.RequestValues, FavoriteMail.ResponseValue> {

    private final MailRepository mMailRepository;

    public FavoriteMail(@NonNull MailRepository mailRepository){
        mMailRepository = checkNotNull(mailRepository, "mailRepository cannot be null");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        String readMail = values.getReadMail();
        mMailRepository.favoriteMail(readMail);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues{

        private final String mReadMail;

        public RequestValues(@NonNull String readMail){
            mReadMail = checkNotNull(readMail, "readMail cannot be null");
        }

        public String getReadMail(){
            return mReadMail;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue{ }
}
