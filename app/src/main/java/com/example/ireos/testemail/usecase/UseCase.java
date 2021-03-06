package com.example.ireos.testemail.usecase;

/**
 * Created by tav on 01/03/2018.
 */

public abstract class UseCase <Q extends UseCase.RequestValues, P extends UseCase.ResponseValue> {

    private Q mRequestValues;

    private UseCaseCallback<P> mUseCaseCallback;

    public void setRequestValues(Q requestValues){
        mRequestValues = requestValues;
    }

    public Q getRequestValues(){
        return mRequestValues;
    }

    public UseCaseCallback<P> getUseCaseCallback(){
        return mUseCaseCallback;
    }

    public void setUseCaseCallback(UseCaseCallback<P> useCaseCallback){
        mUseCaseCallback = useCaseCallback;
    }

    void run(){
        executeUseCase(mRequestValues);
    }

    protected abstract void executeUseCase(Q requestValues);

    public interface RequestValues{

    }

    public interface ResponseValue{

    }

    public interface UseCaseCallback<R>{
        void onSuccess(R response);
        void onError();
    }
}
