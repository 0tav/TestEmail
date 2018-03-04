package com.example.ireos.testemail.sent.domain.usecase;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailRepository;
import com.example.ireos.testemail.usecase.UseCase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 03/03/2018.
 */

public class DeleteActiveMail
        extends UseCase<DeleteActiveMail.RequestValues, DeleteActiveMail.ResponseValue> {

    private final MailRepository mMailRepository;

    public DeleteActiveMail(@NonNull MailRepository mailRepository){
        mMailRepository = checkNotNull(mailRepository, "mailRepository cannot be null");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mMailRepository.deleteActiveMail();
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static class RequestValues implements UseCase.RequestValues{ }

    public static class ResponseValue implements UseCase.ResponseValue{ }
}
