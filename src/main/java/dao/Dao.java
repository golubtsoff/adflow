package dao;

import java.util.List;

public interface Dao<T> {
    T get(long id);
    List<T> getAll();
    long create(T t);
    void update(T t);
    T delete(long id);
    void deleteAll();
    Class<T> getParameterizedClass();
}
