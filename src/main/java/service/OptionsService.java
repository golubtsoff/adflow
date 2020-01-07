package service;

import dao.DaoFactory;
import dao.DbAssistant;
import entity.statistics.Options;
import exception.DbException;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import java.util.List;

public abstract class OptionsService {

    public static void initOptions() throws DbException {
        Transaction transaction = DbAssistant.getTransaction();
        try {
            List<Options> optionsList = DaoFactory.getOptionsDao().getAll();
            if (optionsList.isEmpty()){
                Options options = new Options(Options.durationShowDefault);
                DaoFactory.getOptionsDao().create(options);
            }
            transaction.commit();
        } catch (HibernateException | NoResultException | NullPointerException e) {
            DbAssistant.transactionRollback(transaction);
            throw new DbException(e);
        }
    }
}
