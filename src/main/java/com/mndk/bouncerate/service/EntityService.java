package com.mndk.bouncerate.service;

import java.util.List;

public interface EntityService<T> {

    // ====== GETTERS =====
    T getOne(int id);
    List<T> getPage(int countPerPage, int pageNumber);
    int getCount();

    // ===== SETTERS =====
    void addOne(T objectPart);
    void addManyRandom(int count);
    void updateOne(int id, T objectPart);

    // ===== REMOVERS =====
    void deleteOne(int id);

}
