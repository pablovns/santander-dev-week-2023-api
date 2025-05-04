package me.dio.service;

import java.util.List;

public interface CrudService<I, T> {
    List<T> findAll();
    T findById(I i);
    T create(T entity);
    T update(I i, T entity);
    void delete(I i);
}
