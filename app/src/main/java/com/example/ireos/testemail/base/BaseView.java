package com.example.ireos.testemail.base;

/**
 * Created by tav on 01/03/2018.
 */

public interface BaseView <T extends BasePresenter> {

    void setPresenter(T presenter);
}
