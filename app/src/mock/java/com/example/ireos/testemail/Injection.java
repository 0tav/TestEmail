package com.example.ireos.testemail;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.ireos.testemail.data.FakeMailRemoteDataResource;
import com.example.ireos.testemail.data.MailRepository;
import com.example.ireos.testemail.data.local.MailDatabase;
import com.example.ireos.testemail.data.local.MailLocalDataSource;
import com.example.ireos.testemail.sent.domain.usecase.DeleteActiveMail;
import com.example.ireos.testemail.sent.domain.usecase.FavoriteMail;
import com.example.ireos.testemail.sent.domain.usecase.GetMails;
import com.example.ireos.testemail.senteditmail.domain.DeleteMail;
import com.example.ireos.testemail.senteditmail.domain.GetMail;
import com.example.ireos.testemail.senteditmail.domain.SentMail;
import com.example.ireos.testemail.sent.domain.filter.FilterFactory;
import com.example.ireos.testemail.sent.domain.usecase.ActivateMail;
import com.example.ireos.testemail.usecase.UseCaseHandler;
import com.example.ireos.testemail.util.AppExecutors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by tav on 02/03/2018.
 */

public class Injection {

    public static MailRepository provideMailRepository(@NonNull Context context){
        checkNotNull(context);
        MailDatabase database = MailDatabase.getInstance(context);
        return MailRepository.getInstance(FakeMailRemoteDataResource.getInstace(),
                MailLocalDataSource.getInstance(new AppExecutors(),
                        database.mailDao()));
    }

    public static GetMails provideGetMails(@NonNull Context context){
        return new GetMails(provideMailRepository(context), new FilterFactory());
    }

    public static UseCaseHandler provideUseCaseHandler(){
        return UseCaseHandler.getInstance();
    }

    public static GetMail provideGetMail(@NonNull Context context){
        return new GetMail(Injection.provideMailRepository(context));
    }

    public static SentMail provideSentMail(@NonNull Context context) {
        return new SentMail(Injection.provideMailRepository(context));
    }

    public static FavoriteMail provideReadMail(@NonNull Context context) {
        return new FavoriteMail(Injection.provideMailRepository(context));
    }

    public static ActivateMail provideUnreadMail(@NonNull Context context) {
        return new ActivateMail(Injection.provideMailRepository(context));
    }

    public static DeleteMail provideDeleteMail(@NonNull Context context) {
        return new DeleteMail(Injection.provideMailRepository(context));
    }

    public static DeleteActiveMail provideDeleteActiveMail(@NonNull Context context){
        return new DeleteActiveMail(Injection.provideMailRepository(context));
    }
}
