package dao;

public interface ClientDao<T> extends Dao<T> {
    T getByUserId(Long userId);
}

