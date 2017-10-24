package ru.chertenok.filerepository.client;

import ru.chertenok.filerepository.common.ConfigCommon;
import ru.chertenok.filerepository.common.Message;
import ru.chertenok.filerepository.common.MyCalculation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    public Client() {
        try {
            Socket server = new Socket(ConfigCommon.getServerUrl(), ConfigCommon.getServerPort());
            System.out.println("connected to server");
            ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());

            ObjectInputStream in = new ObjectInputStream(server.getInputStream());
            System.out.println("пишем дата реквест");

            out.writeObject(new Message());
            out.flush();
            System.out.println("читаем ответ");
            System.out.println(in.readObject());
            System.out.println("пишем  калькулятион");
            out.writeObject(new MyCalculation(2));
            out.flush();
            System.out.println("ждем ответ");
            System.out.println(in.readObject());
            System.out.println("закрываем соединение");
            server.close();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
