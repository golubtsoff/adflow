package dao.impl;

import dao.AbstractDao;
import entity.statistics.Session;
import entity.users.partner.Platform;

import java.util.List;

public class SessionDao extends AbstractDao<Session> {

    public SessionDao(){
        super(Session.class);
    }

    public List<Session> getByPlatformId(long platformId){
        return getAll("platform_id", platformId);
    }
}
