package ru.chertenok.filerepository.common.config;

public class ConfigCommon {
    private static final String DEFAULT_SERVER_URL = "localhost";
    private static final int DEFAULT_SERVER_PORT = 8000;
    private static String  serverURL = DEFAULT_SERVER_URL;
    private static int  serverPort = DEFAULT_SERVER_PORT;


    private ConfigCommon() {
    }

    public static String getServerURL() {
        return serverURL;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerURL(String serverURL) {
        ConfigCommon.serverURL = serverURL;
    }

    public static void setServerPort(int serverPort) {
        ConfigCommon.serverPort = serverPort;
    }
}
