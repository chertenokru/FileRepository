package ru.chertenok.filerepository.client.server;

public class Server {
    private BDHandler bdHandler;

    public Server() {
    }

    public void run(){
        BDHandler.init(Config.CONNECT_TO_BD_STRING,Config.PATH_TO_BD);


    }

    public void stop(){
        BDHandler.close();
    }
    //private Vector<ClientHandler> clients;



}
