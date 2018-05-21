package ru.chertenok.filerepository.common.client;

import ru.chertenok.filerepository.common.FileInfo;
import ru.chertenok.filerepository.common.config.ConfigCommon;
import ru.chertenok.filerepository.common.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.chertenok.filerepository.common.utils.MessageUtils.readMessage;
import static ru.chertenok.filerepository.common.utils.MessageUtils.sendMessage;

public class Client {
    private final Logger log = Logger.getGlobal();
    private Socket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isLoggIn;
    private boolean isConnected;
    private String localFilesPath = "/";

    public Client() {
        connect();
    }

    public boolean isLoggIn() {
        return isLoggIn;
    }

    public String getLocalFilesPath() {
        return localFilesPath;
    }

    public void setLocalFilesPath(String localFilesPath) {
        this.localFilesPath = localFilesPath;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean connect() {
        if (isConnected) return isConnected;
        try {
            log.log(Level.INFO, "connect to socket server...");
            server = new Socket(ConfigCommon.getServerURL(), ConfigCommon.getServerPort());
            log.log(Level.INFO, "connected to server " + ConfigCommon.getServerURL() + ":" + ConfigCommon.getServerPort());
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
            isConnected = true;
        } catch (IOException e) {
            log.log(Level.SEVERE, "socket is not connected (" + ConfigCommon.getServerURL() + ":" + ConfigCommon.getServerPort() + "): " + e);
            isConnected = false;
        }
        return isConnected;
    }

    public void disconnect() {
        if (!isConnected) {
            log.log(Level.INFO, "not connected");
            return;
        }

        try {
            log.log(Level.INFO, "send closeMessage to server");
            sendMessage(new MessageClose(), out);
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "error closing in/out stream: " + e);
        } finally {
            if (server.isConnected()) try {
                server.close();
            } catch (IOException e) {
                log.log(Level.SEVERE, "error closing server: " + e);
            }
            isConnected = false;
            isLoggIn = false;
            log.log(Level.INFO, "socket closed");

        }

    }

    public String uploadFile(String file) {
        if (!isConnected) return "not connected";
        if (!isLoggIn) return "not login";
        MessageFile m = null;
        try {
            m = new MessageFile(file);
        } catch (IOException e) {
            log.log(Level.SEVERE, "не удалось отправить файл: " + e);
            return "не удалось отправить файл: " + e;
        }
        sendMessage(m, out);
        return processMessage(readMessage(in));
    }

    public String register(String login, String password, boolean newUser) {
        if (!isConnected) return "not connected";

        if (sendMessage(new MessageLogin(login, password, newUser), out)) {
            Message m = readMessage(in);
            if (m instanceof MessageResult) {
                MessageResult mr = (MessageResult) m;
                if (mr.success) {
                    isLoggIn = true;
                    return mr.message;
                } else {
                    return mr.message;
                }
            } else {
                return processMessage(m);

            }}
        else {
            disconnect();
            return "connection lost ...";
        }

    }

    public FileInfo[] getFileList() {
        if (isLoggIn) {
            if (sendMessage(new MessageGetList(), out)) {
                Message m = readMessage(in);
                if (m instanceof MessageFileList) {
                    return ((MessageFileList) m).fileInfos;
                } else processMessage(m);
            } else {
                disconnect();
                return new FileInfo[0];
            }
        }
        return null;
    }

    private String processMessage(Message message) {
        if (message == null) return "";

        if (message instanceof MessageClose) {
            log.log(Level.INFO, "server closed session ");
            disconnect();
            return "server closed session ";
        }
        if (message instanceof MessageResult) {
            MessageResult m = (MessageResult) message;
            if (m.success) {
                return "Ok. " + m.message;
            } else {
                return "Error. " + m.message;
            }
        }

        return "";
    }

    public void logOut() {
        if (sendMessage(new MessageLogOut(), out)) {
            isLoggIn = false;
        } else {
            disconnect();
        }
    }


    public String getFile(FileInfo f, boolean replace) {
        if (sendMessage(new MessageGetFile(f), out)) {
            Message m = readMessage(in);
            if (m instanceof MessageResultFile) {
                MessageResultFile rf = (MessageResultFile) m;
                Path p_dir = Paths.get(localFilesPath);
                if (!Files.exists(p_dir)) try {
                    Files.createDirectories(p_dir);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "error creating destionation directory: " + e);
                    return "error creating destionation directory: " + e;
                }
                Path p = Paths.get(localFilesPath + rf.fileInfo.fileName);
                if (Files.exists(p)) {
                    if (replace) try {
                        Files.delete(p);
                    } catch (IOException e) {
                        log.log(Level.SEVERE, "error deleting file " + p + ": " + e);
                        return "error deleting file " + p + ": " + e;
                    }
                    else {
                        log.log(Level.SEVERE, "file exist: " + p);
                        return "file exist " + p;
                    }
                }
                try {
                    Files.write(p, rf.data);
                    log.log(Level.INFO, "download and saved file: " + p);
                    return "download and saved file: " + p;
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Error saving file: " + e);
                    return "Error saving file: " + e;
                }
            } else {
                processMessage(m);
                return processMessage(m);
            }
        } else {
            disconnect();
            return "connection lost ...";
        }
    }


    public String deleteFile(FileInfo fileInfo) {
        sendMessage(new MessageFileDelete(fileInfo),out);
            return processMessage(readMessage(in));

    }
}
