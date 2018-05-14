package ru.chertenok.filerepository.server;

import ru.chertenok.filerepository.common.config.ConfigCommon;
import ru.chertenok.filerepository.server.bd.BDHandler;
import ru.chertenok.filerepository.server.config.ConfigServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private Logger log = Logger.getGlobal();
    private ServerSocket server;
    private boolean isStop;
    private List<ClientConnection> listClients = new Vector<ClientConnection>();

    public Server() {
    }

    public void run() {
        try {
            log.log(Level.INFO, "starting...");
            log.log(Level.INFO, "connect to bd...");
            BDHandler.init(ConfigServer.CONNECT_TO_BD_STRING, ConfigServer.PATH_TO_BD);
            startSocket();

        } catch (SQLException e) {
           log.log(Level.SEVERE,"sql error on connect: "+e);
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE,"bd driver class not fount: "+e);
        }
    }

    private void startSocket() {
        try {
            log.log(Level.INFO, "Socket starting... ");
            server = new ServerSocket(ConfigCommon.getServerPort());
            log.log(Level.INFO, "Server started on port " + ConfigCommon.getServerPort());
            Socket client;
            ClientConnection clientConnection;

            while (!isStop) {
                log.log(Level.INFO, "Server wait connections... ");
                client = server.accept();
                clientConnection = new ClientConnection(client);
                addClient(clientConnection);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.toString());
            e.printStackTrace();
        } finally {
            stop();
            try {
                server.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "Error closing ServerSocket : " + e.toString());
                e.printStackTrace();
            }

        }
    }

    private void addClient(ClientConnection clientConnection) {
        listClients.add(clientConnection);
        clientConnection.setServer(this);
        clientConnection.start();
    }

    public void removeClient(ClientConnection client) {
        listClients.remove(client);
    }

    public void stopServer() {
        isStop = true;
    }


    private void stop() {
        for (int i = listClients.size(); i <= 0; i--) {
            log.log(Level.INFO, "stoping client " + listClients.get(i).toString());
            listClients.get(i).stopServer();
            listClients.remove(i);
        }

        try {
            log.log(Level.INFO, "stoping bd...");
            BDHandler.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "error closing bd :" + e);
        }
    }


}
