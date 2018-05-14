package ru.chertenok.filerepository.server;

import ru.chertenok.filerepository.server.config.ConfigServer;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    public static void main(String[] args) {
        try {

            Logger.getGlobal().setLevel(ConfigServer.LOG_GLOBAL_LEVEL);

            FileHandler handler = new FileHandler(ConfigServer.LOG_FILE_NAME, ConfigServer.LOG_FILE_SIZE, 1, true);
            handler.setLevel(ConfigServer.LOG_FILE_LEVEL);
            handler.setFormatter(new SimpleFormatter());
            Logger.getGlobal().addHandler(handler);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "log file '" + ConfigServer.LOG_FILE_NAME + "'not created :" + e);
        }

        new Server().run();
    }
}
