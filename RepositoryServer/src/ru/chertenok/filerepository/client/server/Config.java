package ru.chertenok.filerepository.client.server;

import java.sql.DriverManager;

public class Config {
    public static final int PORT_NUM = 8189;
    public static final String PATH_TO_BD = "repository.db";
    public static final String BD_DRIVER_NAME ="org.sqlite.JDBC";
    public static final String CONNECT_TO_BD_STRING = "jdbc:sqlite:%s";




    private Config() {
    }
}
