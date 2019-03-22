package dao.impl;

import dao.AbstractDao;
import entity.users.user.Token;

public class TokenDao extends AbstractDao<Token> {

    public TokenDao(){
        super(Token.class);
    }
}
