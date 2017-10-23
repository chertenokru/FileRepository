package ru.chertenok.filerepository.server;

public class Server {
    private BDHandler bdHandler;

    public Server() {
    }

    public void run(){
        BDHandler.init(ConfigServer.CONNECT_TO_BD_STRING, ConfigServer.PATH_TO_BD);


    }

    public void stop(){
        BDHandler.close();
    }
    //private Vector<ClientHandler> clients;



}
