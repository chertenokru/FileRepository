package ru.chertenok.filerepository.common.utils;

import ru.chertenok.filerepository.common.messages.Message;
import ru.chertenok.filerepository.common.messages.MessageClose;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageUtils {
    private static Logger log = Logger.getGlobal();




    public static boolean sendMessage( Message message,ObjectOutputStream out)  {
        log.log(Level.INFO, "send " + message.getClass().getSimpleName());
        try {
            out.writeObject(message);
            out.flush();
            return true;
        }
        catch (SocketException e){
            log.log(Level.SEVERE,"error sending messages: "+e);
            return false;
        }
        catch (IOException e) {
            log.log(Level.SEVERE,"error sending messages: "+e);
            return false;
        }

    }

    public static Message readMessage(ObjectInputStream in) {
        Object object = null;
        try {
            object = in.readObject();
            log.log(Level.INFO, "received " + object.getClass().getSimpleName());
        }
        catch (EOFException e){
            object = new MessageClose();
        }

        catch (IOException e) {
            log.log(Level.SEVERE,"error reciving message: "+e);
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE,"recived unknow message: "+e);
        }

        if (object instanceof Message)
            return (Message) object;
        else return null;
    }



    private MessageUtils() {
    }
}
