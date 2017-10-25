package ru.chertenok.filerepository.client;


import ru.chertenok.filerepository.client.config.ConfigClient;
import ru.chertenok.filerepository.client.utils.Utils;
import ru.chertenok.filerepository.common.config.ConfigCommon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Main extends JFrame {
    private final Client client;
    private Logger log = Logger.getGlobal();
    private JPanel pRoot;
    private JTextField tfLogin;
    private JPasswordField pfPassword;
    private JCheckBox cbNewUser;
    private JPanel statusConnection;
    private JPanel statusLogin;
    private JTextField tfIP;
    private JTextField tfPort;
    private JButton bConnect;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        super(ConfigClient.TITLE);
        configLogger();
        client = new Client();
        configWindow();
    }

    private void configLogger() {
        try {
            Logger.getGlobal().setLevel(ConfigClient.LOG_GLOBAL_LEVEL);
            FileHandler handler = new FileHandler(ConfigClient.LOG_FILE_NAME, ConfigClient.LOG_FILE_SIZE, 1, true);
            handler.setLevel(ConfigClient.LOG_FILE_LEVEL);
            handler.setFormatter(new SimpleFormatter());
            Logger.getGlobal().addHandler(handler);
        } catch (IOException e) {
            log.log(Level.SEVERE, "log file '" + ConfigClient.LOG_FILE_NAME + "'not created :" + e);
        }
    }


    private void configWindow() {
        setSize(ConfigClient.getWindowWIDTH(), ConfigClient.getWindowHeight());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Object[] options = {"Да", "Нет!"};
                int n = JOptionPane.showOptionDialog(e.getWindow(), "Закрыть окно?",
                        "Подтверждение", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (n == 0) {
                    log.log(Level.INFO, "user closed window");
                    e.getWindow().setVisible(false);
                    if (client != null) {
                        log.log(Level.INFO, "stoping client");
                        client.disconnect();
                        log.log(Level.INFO, "client stopped");
                    }
                    log.log(Level.INFO, "exit");
                    System.exit(0);
                }
            }
        });
        setLocationRelativeTo(null);

        pRoot = new JPanel();
        pRoot.setLayout(new GridBagLayout());
        //  root.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        GridBagConstraints constr = new GridBagConstraints();
        constr.insets = new Insets(5, 5, 5, 5);
        constr.gridy = 0;

        constr.gridx = 0;
        //createLocalPanel(constr);
        constr.gridx ++;
        createServerPanel(constr);
        constr.gridx ++;
        createUserPanel(constr);

        setContentPane(pRoot);
        pack();
        setVisible(true);
    }

    private void createUserPanel(GridBagConstraints constr) {
        JPanel pUsersOperation = new JPanel();
        pUsersOperation.setBorder(Utils.getBorderWithTitle("|  Setting  |"));
        pUsersOperation.setPreferredSize(new Dimension(400, 600));
        pUsersOperation.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        pUsersOperation.add(new JLabel("Server status: "), c);
        c.gridx = 1;
        statusConnection = new JPanel();
        pUsersOperation.add(statusConnection, c);

        c.gridy++;
        c.gridx = 0;
        pUsersOperation.add(new JLabel("Server ip: "), c);
        c.gridx = 1;
        tfIP = new JTextField(ConfigCommon.getServerURL(), 10);
        pUsersOperation.add(tfIP, c);
        c.gridy++;
        c.gridx = 0;
        pUsersOperation.add(new JLabel("Server port: "), c);
        c.gridx = 1;
        tfPort = new JTextField("" + ConfigCommon.getServerPort(), 10);
        pUsersOperation.add(tfPort, c);
        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        bConnect = new JButton("Connect");
        bConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (client.isConnected())
                {
                    client.disconnect();
                    updateStatus();
                }else {
                    ConfigCommon.setServerURL(tfIP.getText());
                    ConfigCommon.setServerPort(Integer.valueOf(tfPort.getText()));
                    client.connect();
                    updateStatus();
                }
            }
        });

        pUsersOperation.add(bConnect, c);
        c.gridwidth = 1;
        c.gridy++;
        c.gridx = 0;
        pUsersOperation.add(new JLabel("User status: "), c);
        c.gridx = 1;
        statusLogin = new JPanel();

        pUsersOperation.add(statusLogin, c);
        updateStatus();

        c.gridy++;
        c.gridx = 0;
        pUsersOperation.add(new JLabel("Login"), c);
        c.gridx = 1;
        tfLogin = new JTextField(10);
        //tfLogin.setColumns(20);
        pUsersOperation.add(tfLogin, c);
        c.gridx = 0;
        c.gridy++;
        pUsersOperation.add(new JLabel("Password"), c);
        c.gridx = 1;
        pfPassword = new JPasswordField(10);
        //pfPassword. setColumns(20);

        pUsersOperation.add(pfPassword, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        cbNewUser = new JCheckBox("New user", false);
        pUsersOperation.add(cbNewUser, c);
        c.gridx = 0;
        c.gridy++;

        JButton b = new JButton("Login/Registry");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.setText(client.register(tfLogin.getText(), String.copyValueOf(pfPassword.getPassword()),cbNewUser.isSelected()));
            }
        });
        pUsersOperation.add(b, c);
        c.gridwidth = 1;

        constr.anchor = GridBagConstraints.NORTH;
        pRoot.add(pUsersOperation, constr);
    }

    private void updateStatus() {
        if (client.isConnected()) {
            statusConnection.setBackground(Color.GREEN);
            bConnect.setText("Disconnect");
            tfIP.setEnabled(false);
            tfPort.setEnabled(false);
        } else {
            statusConnection.setBackground(Color.RED);
            bConnect.setText("Connect");
            tfIP.setEnabled(true);
            tfPort.setEnabled(true);

        }
        if (client.isLoggIn()) {
            statusLogin.setBackground(Color.GREEN);
        } else {
            statusLogin.setBackground(Color.RED);
        }
    }

    private void createServerPanel(GridBagConstraints constr) {
        JPanel pServer = new JPanel();
        pServer.setBorder(Utils.getBorderWithTitle("|  Server storage  |"));
        pServer.setPreferredSize(new Dimension(300, 600));

        pRoot.add(pServer, constr);
    }

    private void createLocalPanel(GridBagConstraints constr) {
        JPanel pLocal = new JPanel();
        pLocal.setBorder(Utils.getBorderWithTitle("|  Local storage  |"));
        pLocal.setPreferredSize(new Dimension(300, 600));
        pRoot.add(pLocal, constr);
    }

}
