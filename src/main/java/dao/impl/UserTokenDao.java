package dao.impl;

import dao.AbstractDao;
import entity.users.user.UserToken;

public class UserTokenDao extends AbstractDao<UserToken> {

    public UserTokenDao(){
        super(UserToken.class);
    }
}
