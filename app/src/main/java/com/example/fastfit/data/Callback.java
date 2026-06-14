package com.example.fastfit.data;

/** Minimal async result callback used across the repository layer. */
public interface Callback<T> {
    void onSuccess(T data);
    void onError(String message);
}
