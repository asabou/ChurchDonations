package com.rurbisservices.churchdonation.service.abstracts;

import java.util.List;

public interface IService<T, ID> {
    T create(T t);
    T update(T t);
    void delete(ID id);
    List<T> findAll();
    T findById(ID id);
}
