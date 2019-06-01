package dao;

public interface CustomerPartnerDao<T> extends Dao<T> {
    T getByUserId(Long userId);
}

