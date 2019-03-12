package main;

import entity.user.Person;
import dao.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Main {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        Class cl = userDao.getParameterizedClass();
    }
}
