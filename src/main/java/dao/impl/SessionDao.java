package dao.impl;

import dao.AbstractDao;
import entity.statistics.Session;

public class SessionDao extends AbstractDao<Session> {

    public SessionDao(){
        super(Session.class);
    }
}
