package ru.chertenok.filerepository.client.config;

import java.util.logging.Level;

// todo external setting file
public class ConfigClient {
    private final static int WINDOW_WIDTH = 800;
    private final static int WINDOW_HEIGHT = 640;
    private final static String DEFAULT_LOCAL_FILES_PATH = "LocalFiles/";

    public static final String TITLE = "Repository Client";
    public static final String LOG_FILE_NAME = "log.txt";
    public static final int LOG_FILE_SIZE = 1024;
    public static final Level LOG_GLOBAL_LEVEL = Level.INFO;
    public static final Level LOG_FILE_LEVEL = Level.ALL;

    private static  String localFilesPath = DEFAULT_LOCAL_FILES_PATH;

    public static String getLocalFilesPath() {
        return localFilesPath;
    }

    public static void setLocalFilesPath(String localFilesPath) {
        ConfigClient.localFilesPath = localFilesPath;
    }

    public static int getWindowWIDTH() {
        return WINDOW_WIDTH;
    }

    public static int getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    private ConfigClient() {
    }

}
