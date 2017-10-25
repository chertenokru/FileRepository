package ru.chertenok.filerepository.server;

import ru.chertenok.filerepository.common.messages.*;
import ru.chertenok.filerepository.server.bd.BDHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.chertenok.filerepository.common.utils.MessageUtils.readMessage;
import static ru.chertenok.filerepository.common.utils.MessageUtils.sendMessage;


public class ClientConnection extends Thread {
    private Logger log = Logger.getGlobal();
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isLoggIn;
    private String userLogin;
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
            log.log(Level.INFO, "client connection ready (" + client.getPort() + ")");
            while (!isStop) {
                processMessage(readMessage(in));
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, "какая-то ошибка: " + e);
            isStop = true;

        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "error closing in/out stream: " + e);
            } finally {
                if (client.isConnected()) try {
                    client.close();
                } catch (IOException e) {
                    log.log(Level.SEVERE, "error closing client: " + e);
                }
            }
        }

    }

    private void processMessage(Message message) {
        if (message == null) return;
        // =============   login  ============================
        if (message instanceof MessageLogin) {
            MessageLogin m = (MessageLogin) message;
            // уже зареган
            if (isLoggIn) {
                if (userLogin.equals(m.userLogin))
                   sendMessage(new MessageResult(true, "user " + m.userLogin + " registered"), out);
                else
                    sendMessage(new MessageResult(false, "client already registered with other login"), out);
                return;
            }

            // проверки
            if (m.userLogin.trim().length()<3)
            {
                log.log(Level.INFO,"length of UserName < 3");
                sendMessage(new MessageResult(false, "length of UserName < 3"), out);
                return;
            }
            if (m.userPassword.trim().length()<5)
            {
                log.log(Level.INFO,"length of User Password < 5");
                sendMessage(new MessageResult(false, "length of User Password < 5"), out);
                return;
            }


            if (m.isNewUser)
                // если новый
                synchronized (BDHandler.class) {
                    try {
                        if (BDHandler.checkName(m.userLogin)) {
                            BDHandler.registerUser(m.userLogin, m.userPassword);
                            isLoggIn = true;
                            userLogin = m.userLogin.trim();
                            log.log(Level.INFO,"user " + m.userLogin + " registered");
                            sendMessage(new MessageResult(true, "user " + m.userLogin + " registered"), out);
                        } else
                        {
                            log.log(Level.INFO,"user " + m.userLogin + " already exist");
                            sendMessage(new MessageResult(false, "user " + m.userLogin + " already exist"), out);
                        }
                    } catch (SQLException e) {
                        log.log(Level.SEVERE, "sql error user " + m.userLogin + " registration: " + e);
                        sendMessage(new MessageResult(false, "internal error"), out);
                    }
                }
            else
                // старый
                {
                    try {
                        if (BDHandler.loginUser(m.userLogin, m.userPassword))
                        {
                            log.log(Level.INFO,"user " + m.userLogin + " login");
                            sendMessage(new MessageResult(true, "user " + m.userLogin + " login"), out);
                            isLoggIn = true;
                            userLogin = m.userLogin.trim();

                        }else
                        {
                            log.log(Level.INFO,"user " + m.userLogin + " not login");
                            sendMessage(new MessageResult(false, "user " + m.userLogin + " not login,check login and password"), out);

                        }

                    } catch (Exception e) {
                        log.log(Level.SEVERE,"internal error: "+e);
                        sendMessage(new MessageResult(false, "internal error"), out);
                    }


                }
        }

        // ===================== close connection ==========================
        if (message instanceof MessageClose) {
            log.log(Level.INFO, "client closed session " + client.getPort() + ")");
            isLoggIn = false;
            isStop = true;
        }

        // =================== log out ==================================
        if (message instanceof MessageLogOut) {
            log.log(Level.INFO, "client logout " + client.getPort() + ")");
            isLoggIn = false;
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
