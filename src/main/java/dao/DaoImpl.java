package dao;

import org.hibernate.LockMode;
import org.hibernate.Session;
import util.DBService;

import java.util.List;

public class DaoImpl<T> implements Dao<T> {

    private Class<T> parameterizedClass;

    DaoImpl(Class<T> cl){
        this.parameterizedClass = cl;
    }

    @Override
    public Class<T> getParameterizedClass() {
        return parameterizedClass;
    }

    @Override
    public T get(long id) {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .get(parameterizedClass, id, LockMode.PESSIMISTIC_READ);
    }

    @Override
    public List<T> getAll() {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createQuery("from " + parameterizedClass.getSimpleName(), parameterizedClass)
                .list();
    }

    @Override
    public long create(T t) {
        return (Long) DBService.getSessionFactory()
                .getCurrentSession()
                .save(t);
    }

    @Override
    public void update(T t) {
        DBService.getSessionFactory().getCurrentSession()
                .update(t);
    }

    @Override
    public T delete(long id) {
        Session session = DBService.getSessionFactory().getCurrentSession();
        T t = session.byId(parameterizedClass).load(id);
        session.delete(t);
        return t;
    }

    @Override
    public void deleteAll() {
        DBService.getSessionFactory()
                .getCurrentSession()
                .createQuery("delete from " + parameterizedClass.getSimpleName())
                .executeUpdate();
    }
}
