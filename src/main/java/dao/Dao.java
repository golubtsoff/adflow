package dao;

import java.util.List;

public interface Dao<T> {
    T get(long id);
    <U> T get(String fieldName, U value);
    <U> List<T> getAll(String fieldName, U value);
    List<T> getAll();
    long create(T t);
    void merge(T t);
    void update(T t);
    T delete(long id);
    void deleteAll();
    Class<T> getParameterizedClass();
}
