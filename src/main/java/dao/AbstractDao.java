package dao;

import org.h2.message.DbException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Queue;

public abstract class AbstractDao<T> implements Dao<T> {

    private Class<T> parameterizedClass;

    protected AbstractDao(Class<T> cl){
        this.parameterizedClass = cl;
    }

    @Override
    public Class<T> getParameterizedClass() {
        return parameterizedClass;
    }

    @Override
    public T get(long id) {
        return DbAssistant.getSessionFactory()
                .getCurrentSession()
                .get(parameterizedClass, id, LockMode.PESSIMISTIC_READ);
    }

    @Override
    public <U> T get(String fieldName, U value){
        return DbAssistant.getSessionFactory()
                .getCurrentSession()
                .createQuery("from " + parameterizedClass.getSimpleName()
                        + " where " + fieldName + " = :value", parameterizedClass)
                .setParameter("value", value)
                .getSingleResult();
    }


    @Override
    public List<T> getAll() {
        return DbAssistant.getSessionFactory()
                .getCurrentSession()
                .createQuery("from " + parameterizedClass.getSimpleName(), parameterizedClass)
                .list();
    }

    @Override
    public <U> List<T> getAll(String fieldName, U value) {
        return DbAssistant.getSessionFactory()
                .getCurrentSession()
                .createQuery("from " + parameterizedClass.getSimpleName()
                        + " where " + fieldName + " = :value", parameterizedClass)
                .setParameter("value", value)
                .list();
    }

    @Override
    public long create(T t) {
        return (Long) DbAssistant.getSessionFactory()
                .getCurrentSession()
                .save(t);
    }

    @Override
    public void update(T t) {
        DbAssistant.getSessionFactory().getCurrentSession().update(t);
    }

    @Override
    public void merge(T t){
        DbAssistant.getSessionFactory().getCurrentSession().merge(t);
    }

    @Override
    public T delete(long id) {
        Session session = DbAssistant.getSessionFactory().getCurrentSession();
        T t = session.byId(parameterizedClass).load(id);
        session.delete(t);
        return t;
    }

    @Override
    public void deleteAll() {
        DbAssistant.getSessionFactory()
                .getCurrentSession()
                .createQuery("delete from " + parameterizedClass.getSimpleName())
                .executeUpdate();
    }
}
