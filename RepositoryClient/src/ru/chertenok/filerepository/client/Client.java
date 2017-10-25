package ru.chertenok.filerepository.client;

import ru.chertenok.filerepository.common.config.ConfigCommon;
import ru.chertenok.filerepository.common.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.chertenok.filerepository.common.utils.MessageUtils.readMessage;
import static ru.chertenok.filerepository.common.utils.MessageUtils.sendMessage;

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
            server = new Socket(ConfigCommon.getServerURL(), ConfigCommon.getServerPort());
            log.log(Level.INFO, "connected to server " + ConfigCommon.getServerURL() + ":" + ConfigCommon.getServerPort());
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
            isConnected = true;
        } catch (IOException e) {
            log.log(Level.SEVERE, "socket is not connected (" + ConfigCommon.getServerURL() + ":" + ConfigCommon.getServerPort() + "): " + e);
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
            log.log(Level.INFO, "send closeMessage to server");
            sendMessage(new MessageClose(), out);
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "error closing in/out stream: " + e);
        } finally {
            if (server.isConnected()) try {
                server.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "error closing server: " + e);
            }
            isConnected = false;
            isLoggIn = false;
            log.log(Level.INFO, "socket closed");

        }

    }

    public String register(String login, String password, boolean newUser) {
        if (!isConnected) return "not connected";

        if (sendMessage(new MessageLogin(login, password, newUser), out)) {
            Message m = readMessage(in);
            if (m instanceof MessageResult) {
                MessageResult mr = (MessageResult) m;
                if (mr.success) {
                    isLoggIn = true;
                    return mr.message;
                } else {
                    return mr.message;
                }
            } else {
                processMessage(m);
                return "server not return result";
            }
        } else {
            disconnect();
            return "connection lost ...";
        }

    }


    private void processMessage(Message message) {
        if (message == null) return;

        if (message instanceof MessageClose) {
            log.log(Level.INFO, "server closed session ");
            disconnect();
        }
    }

    public void logOut() {
        if (sendMessage(new MessageLogOut(),out)) {
            isLoggIn = false;
        } else {
            disconnect();
        }


    }
}
