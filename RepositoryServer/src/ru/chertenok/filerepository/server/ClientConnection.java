package ru.chertenok.filerepository.server;

import ru.chertenok.filerepository.common.messages.Message;
import ru.chertenok.filerepository.common.messages.MessageLogin;
import ru.chertenok.filerepository.common.messages.MessageResult;
import ru.chertenok.filerepository.server.bd.BDHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.chertenok.filerepository.common.Utils.*;



public class ClientConnection extends Thread {
    private Logger log = Logger.getGlobal();
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isLoggIn;

    private Server server;
    private boolean isStop;


    public ClientConnection(Socket client) {
        log.log(Level.INFO, "client created: " + client.getPort());
        this.client = client;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void run() {
        try {
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());
            log.log(Level.INFO, "client connection ready");
            while (!isStop) {
                processMessage(readMessage(in));
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, "какая-то ошибка: " + e);
        } finally {
            try {
                if (client.isConnected()) client.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "error closing client: " + e);
            }
        }

    }

    private void processMessage(Message message) {
        if (message == null) return;
        if (message instanceof MessageLogin) {
            MessageLogin m = (MessageLogin) message;
            try {
                BDHandler.registerUser(m.userLogin, m.userPassword);
                sendMessage(new MessageResult(true, "user registred"), out);
            } catch (SQLException e) {
                log.log(Level.SEVERE, "sql error user registration: "+e);
                sendMessage(new MessageResult(false, "internal error"), out);
            }

        }
    }

    public void stopServer() {
        isStop = true;
    }

    @Override
    public String toString() {
        if (client.isConnected())
            return "" + client.getPort();
        else return client.toString();
    }


}
