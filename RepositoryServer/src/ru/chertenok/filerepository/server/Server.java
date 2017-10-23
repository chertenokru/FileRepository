package ru.chertenok.filerepository.server;

import java.sql.SQLException;

public class Server {
    private BDHandler bdHandler;

    public Server() {
    }

    public void run(){
        try {
            BDHandler.init(ConfigServer.CONNECT_TO_BD_STRING, ConfigServer.PATH_TO_BD);
            BDHandler.registerUser("hghjrgrg'l;l;rgrgh","khjrgr'grkhk");
            BDHandler.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public void stop(){
        try {
            BDHandler.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //private Vector<ClientHandler> clients;



}
