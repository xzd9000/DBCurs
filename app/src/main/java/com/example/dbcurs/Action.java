package com.example.dbcurs;

public interface Action<T> {
    public void commit(T t);
}
