package ru.chertenok.filerepository.server;

import ru.chertenok.filerepository.common.ConfigCommon;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private BDHandler bdHandler;
    private ServerSocket server;
    private Logger log = Logger.getGlobal();
    private boolean isStop;

    public Server() {
    }

    public void run(){
        try {
            BDHandler.init(ConfigServer.CONNECT_TO_BD_STRING, ConfigServer.PATH_TO_BD);
         //   BDHandler.registerUser("hghjrgrg'l;l;rgrgh","khjrgr'grkhk");
            start();


        } catch (SQLException e) {
            e.printStackTrace();
            log.log(Level.SEVERE,e.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.log(Level.SEVERE,e.toString());
        } finally
        {
            try {
                BDHandler.close();
            } catch (SQLException e) {
                e.printStackTrace();
                log.log(Level.SEVERE,e.toString());
            }
        }


    }

    private void start(){
        try {
            log.log(Level.INFO,"Server starting... ");
            server = new ServerSocket(ConfigCommon.getServerPort());
            log.log(Level.INFO,"Server started on port "+ConfigCommon.getServerPort());
            while (!isStop)
            {
                log.log(Level.INFO,"Server wait connections... ");
                new ServerConnection(server.accept()).start();
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.toString());
            e.printStackTrace();
        }
        finally{
            try {
                stop();
                server.close();
            } catch (IOException e) {
                log.log(Level.SEVERE,"Error closing ServerSocket : "+e.toString());
                e.printStackTrace();
            }

        }
    }

    public void stopServer(){
        isStop = true;
    }


    private void stop(){
        try {
            // todo: закрытие клиентов

            BDHandler.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //private Vector<ClientHandler> clients;



}
