package ru.chertenok.filerepository.server.config;

import java.util.logging.Level;

// todo external setting file
public class ConfigServer {
    public static final int PORT_NUM = 8189;
    public static final String PATH_TO_BD = "repository.db";
    public static final String BD_DRIVER_NAME = "org.sqlite.JDBC";
    public static final String CONNECT_TO_BD_STRING = "jdbc:sqlite:%s";
    private static final String DEFAULT_FILE_STORE ="file_store/";
    public static final String LOG_FILE_NAME = "server_log.txt";
    public static final int LOG_FILE_SIZE = 1024;
    public static final Level LOG_GLOBAL_LEVEL = Level.INFO;
    public static final Level LOG_FILE_LEVEL = Level.ALL;
    public static final String FILE_EXT = ".dat";

    private static String fileStorege = DEFAULT_FILE_STORE;

    public static String getFileStorege() {
        return fileStorege;
    }

    public static void setFileStorege(String fileStorege) {
        ConfigServer.fileStorege = fileStorege;
    }

    private ConfigServer() {
    }
}
