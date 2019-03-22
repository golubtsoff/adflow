package dao.impl;

import dao.AbstractDao;
import entity.statistics.Request;

public class RequestDao extends AbstractDao<Request> {

    public RequestDao(){
        super(Request.class);
    }
}
