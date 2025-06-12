package com.ru.ami.hse.elgupo.tinder.repository;

public interface TinderCallback<T>{
    void onSuccess(T request);

    void onError(Throwable t);
}
