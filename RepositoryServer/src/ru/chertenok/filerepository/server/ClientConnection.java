package ru.chertenok.filerepository.server;

import ru.chertenok.filerepository.common.messages.*;
import ru.chertenok.filerepository.server.bd.BDHandler;
import ru.chertenok.filerepository.server.config.ConfigServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
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
             login((MessageLogin) message);
             return;
        }

        // ===================== close connection ==========================
        if (message instanceof MessageClose) {
            log.log(Level.INFO, "client closed session " + client.getPort() + ")");
            isLoggIn = false;
            isStop = true;
            return;
        }

        // =================== log out ==================================
        if (message instanceof MessageLogOut) {
            log.log(Level.INFO, "client logout " + client.getPort() + ")");
            isLoggIn = false;
            return;
        }

        // ========================= command for registered user only
        if (!isLoggIn) {
            sendMessage(new MessageResult(false, "user  not login"), out);
            return;
        }

        // ================= load file ============================
        if (message instanceof MessageFile)
        {
            loadFile((MessageFile) message);
            return;
        }
        // ============= get file list ============================
        if (message instanceof MessageGetList)
        {
            try {
                sendMessage(new MessageFileList(BDHandler.getFileList(userLogin)),out);
                log.log(Level.INFO,"send file list to user "+userLogin);

            } catch (Exception e) {
                log.log(Level.SEVERE,"file list create error - "+e);
                sendMessage(new MessageResult(false,"internal error"),out);
            }
        }
        // ======================== get file ======================
        if (message instanceof MessageGetFile)
        {
            sendFile((MessageGetFile) message);
        }

        }

    private void sendFile(MessageGetFile m) {
        try {
            if (!BDHandler.checkFileExist(m.fileInfo.fileName,userLogin))
            {
                log.log(Level.SEVERE,"error checking file in bd: file not exist "+m.fileInfo.fileName+" user "+userLogin);
                sendMessage(new MessageResult(false,"file "+m.fileInfo.fileName+" not exist"),out);
                return;
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE,"error checking file in bd: "+e);
            sendMessage(new MessageResult(false,"internal error"),out);
        }

        Path p = Paths.get(ConfigServer.getFileStorege()+m.fileInfo.ID+".dat");
        if (!Files.exists(p))
        {
            log.log(Level.SEVERE,"file "+p+" не существует" );
            sendMessage(new MessageResult(false,"internal error file "+m.fileInfo.fileName+" not exist in repository"),out);
        } else
        {
            try {
                sendMessage(new MessageResultFile(m.fileInfo,Files.readAllBytes(p)),out);
                log.log(Level.INFO,"file sended "+m.fileInfo.fileName);
            } catch (IOException e) {
                log.log(Level.SEVERE,"error sending file "+m.fileInfo.fileName);
                sendMessage(new MessageResult(false,"internal error reading file "+m.fileInfo.fileName+""),out);
            }
        }
    }

    private void login(MessageLogin message) {
        MessageLogin m = message;
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

    private void loadFile(MessageFile message) {
        // принимаем
        MessageFile messageFile = message;
        if (!checkFileExist()) {
            try {
                try {
                    messageFile.fileInfo.ID = BDHandler.getFileID(messageFile.fileInfo.SourceFileName,userLogin);
                } catch (Exception e) {
                    log.log(Level.SEVERE,"error get FileID: "+e);
                    sendMessage(new MessageResult(false,"internal error"),out);
                    return;
                }
                // существует ли путь
                Path path_dir = Paths.get(ConfigServer.getFileStorege());
                if (!Files.exists(path_dir)) Files.createDirectories(path_dir);

                Path path = Paths.get(ConfigServer.getFileStorege()+messageFile.fileInfo.ID+ConfigServer.FILE_EXT);
                if (Files.exists(path, LinkOption.NOFOLLOW_LINKS))
                {
                    log.log(Level.SEVERE,"file exist: "+path.toString());
                    sendMessage(new MessageResult(false,"error. file exist?"),out);
                    return;
                }
                else {
                    Files.write(path, messageFile.data);
                    log.log(Level.SEVERE, "file received: " + messageFile.fileInfo.fileName+ " saved as "
                            + path.toString());
                    try {
                        BDHandler.addUserFileToBD(userLogin,message.fileInfo);
                        sendMessage(new MessageResult(true, "file received " + messageFile.fileInfo.fileName), out);
                    } catch (SQLException e) {
                        // if error - delete file from disk
                        log.log(Level.SEVERE, "error save file into bd: " + e);
                        Files.delete(path);
                        sendMessage(new MessageResult(false, "error file receiving " + messageFile.fileInfo.fileName), out);
                    }

                }
            } catch (IOException e) {
                log.log(Level.SEVERE, "error file receiving: " + e);
                sendMessage(new MessageResult(false, "error file receiving " + messageFile.fileInfo.fileName), out);
            }
        }
    }

    private boolean checkFileExist() {
        return false;
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
