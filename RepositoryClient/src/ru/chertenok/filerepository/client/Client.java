package ru.chertenok.filerepository.client;

import ru.chertenok.filerepository.common.Utils;
import ru.chertenok.filerepository.common.config.ConfigCommon;
import ru.chertenok.filerepository.common.messages.Message;
import ru.chertenok.filerepository.common.messages.MessageLogin;
import ru.chertenok.filerepository.common.messages.MessageResult;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.chertenok.filerepository.common.Utils.readMessage;
import static ru.chertenok.filerepository.common.Utils.sendMessage;

public class Client {
    private Logger log = Logger.getGlobal();
    private Socket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isLoggIn;
    private boolean isConnected;

    public Client() {
        connect();
    }

    public boolean isLoggIn() {
        return isLoggIn;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean connect() {
        if (isConnected) return isConnected;
        try {
            log.log(Level.INFO, "connect to socket server...");
            server = new Socket(ConfigCommon.getServerUrl(), ConfigCommon.getServerPort());
            log.log(Level.INFO, "connected to server " + ConfigCommon.getServerUrl() + ":" + ConfigCommon.getServerPort());
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
            isConnected = true;
        } catch (IOException e) {
            log.log(Level.SEVERE, "socket is not connected ("+ ConfigCommon.getServerUrl() + ":" + ConfigCommon.getServerPort()+"): " + e);
            isConnected = false;
        }
        return isConnected;
    }

    public void disconnect() {
        if (!isConnected) {
            log.log(Level.INFO, "not connected");
            return;
        }
        try {
            server.close();
            log.log(Level.INFO, "socket closed");
        } catch (IOException e1) {

            e1.printStackTrace();
        }
    }

    public String register(String login,String password)
    {
        if (!isConnected) return "not connected";

        sendMessage(new MessageLogin(login,password,true),out);
        Message m = readMessage(in);
        if (m instanceof MessageResult)
        {
            MessageResult mr = (MessageResult)m;
            if (mr.success){
                isLoggIn = true;
                return mr.message;
            } else
            {
                return mr.message;
            }
        }  else
            return "server not return resalt";
    }



}
