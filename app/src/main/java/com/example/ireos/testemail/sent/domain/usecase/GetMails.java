package com.example.ireos.testemail.sent.domain.usecase;

import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.MailDataSource;
import com.example.ireos.testemail.data.MailRepository;
import com.example.ireos.testemail.sent.MailFilterType;
import com.example.ireos.testemail.sent.domain.filter.FilterFactory;
import com.example.ireos.testemail.sent.domain.filter.MailFilter;
import com.example.ireos.testemail.sent.domain.model.Mail;
import com.example.ireos.testemail.usecase.UseCase;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 01/03/2018.
 */

public class GetMails extends UseCase<GetMails.RequestValues, GetMails.ResponseValue>{

    private final MailRepository mMailRepository;

    private final FilterFactory mFilterFactory;

    public GetMails(@NonNull MailRepository mailRepository, @NonNull FilterFactory filterFactory){
        mMailRepository = checkNotNull(mailRepository, "mailRepository cannot be null");
        mFilterFactory = checkNotNull(filterFactory, "filterFactory cannot be null");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        if (values.isForceUpdate()){
            mMailRepository.refreshMail();
        }

        mMailRepository.getMail(new MailDataSource.LoadMailCallback() {
            @Override
            public void onMailLoaded(List<Mail> mail) {
                MailFilterType currentFiltering = values.getCurrentFiltering();
                MailFilter mailFilter = mFilterFactory.create(currentFiltering);

                List<Mail> mailFiltered = mailFilter.filter(mail);
                ResponseValue responseValue = new ResponseValue(mailFiltered);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues{
        private final MailFilterType mCurrentFiltering;
        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate, @NonNull MailFilterType currentFiltering){
            mForceUpdate = forceUpdate;
            mCurrentFiltering = checkNotNull(currentFiltering, "currentFiltering cannot be null");
        }

        public boolean isForceUpdate(){
            return mForceUpdate;
        }

        public MailFilterType getCurrentFiltering(){
            return mCurrentFiltering;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue{

        private final List<Mail> mMail;

        public ResponseValue(@NonNull List<Mail> mail){
            mMail = checkNotNull(mail, "mail cannot be null");
        }

        public List<Mail> getMail(){
            return mMail;
        }
    }
}
