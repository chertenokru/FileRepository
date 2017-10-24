package ru.chertenok.filerepository.common;

public class MessageLogin extends Message {
    public final String userLogin;
    public final String userPassword;
    public final boolean isNewUser;

    public MessageLogin(String userLogin, String userPassword,boolean isNewUser) {
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.isNewUser = isNewUser;
    }
}
