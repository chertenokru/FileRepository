package ru.chertenok.filerepository.common;

public class ConfigCommon {
    private static final String SERVER_URL = "localhost";
    private static final int SERVER_PORT = 8000;

    private ConfigCommon() {
    }

    public static String getServerUrl() {
        return SERVER_URL;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }
}
