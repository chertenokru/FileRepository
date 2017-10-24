package ru.chertenok.filerepository.server.config;

import java.util.logging.Level;

public class ConfigServer {
    public static final int PORT_NUM = 8189;
    public static final String PATH_TO_BD = "repository.db";
    public static final String BD_DRIVER_NAME = "org.sqlite.JDBC";
    public static final String CONNECT_TO_BD_STRING = "jdbc:sqlite:%s";

    public static final String LOG_FILE_NAME = "server_log.txt";
    public static final int LOG_FILE_SIZE = 1024;
    public static final Level LOG_GLOBAL_LEVEL = Level.INFO;
    public static final Level LOG_FILE_LEVEL = Level.ALL;


    private ConfigServer() {
    }
}
