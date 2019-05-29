package main;

import java.time.LocalDateTime;

public class AnotherUser {

    private long id;
    private String login;
    private String hash;
    private LocalDateTime creationDateTime;
    private String someFieldNotNull;
    private String someFieldIsNull;

    public AnotherUser(){
        this.id = 56L;
        this.hash = "asdqwe";
        this.creationDateTime = LocalDateTime.now();
        this.someFieldNotNull = "notNull";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getSomeFieldNotNull() {
        return someFieldNotNull;
    }

    public void setSomeFieldNotNull(String someFieldNotNull) {
        this.someFieldNotNull = someFieldNotNull;
    }

    public String getSomeFieldIsNull() {
        return someFieldIsNull;
    }

    public void setSomeFieldIsNull(String someFieldIsNull) {
        this.someFieldIsNull = someFieldIsNull;
    }
}
