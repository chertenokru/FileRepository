package ru.chertenok.filerepository.server;

import ru.chertenok.filerepository.common.Message;
import ru.chertenok.filerepository.common.WorkRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ServerConnection extends Thread{
    private Socket client;

    public ServerConnection(Socket client) throws SocketException {
        this.client = client;
    }

    public void run(){
        try
        {
            System.out.println("client connected");
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            while(true)
            {
                System.out.println("читаем и пишем объекты");
                out.writeObject(processMessage(in.readObject()));
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Object processMessage(Object message){
        System.out.println("что-то прочитали");
        if (message instanceof Message) {
            System.out.println("пишем дату");
            return new java.util.Date();
        }
        else if (message instanceof WorkRequest)
        {
            System.out.println("пишем ворк реквест");
            return ((WorkRequest)message).execute();}
        else {
            System.out.println("неопознанный объект");
            return null;}

    }

}
