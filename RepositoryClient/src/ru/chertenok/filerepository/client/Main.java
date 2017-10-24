package ru.chertenok.filerepository.client;


import ru.chertenok.filerepository.client.config.ConfigClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.*;


public class Main extends JFrame {
    private final Client client;
    private Logger log = Logger.getGlobal();


    public Main() {
        super(ConfigClient.TITLE);
        try {
            Logger.getGlobal().setLevel(ConfigClient.LOG_GLOBAL_LEVEL);
            FileHandler handler = new FileHandler(ConfigClient.LOG_FILE_NAME,ConfigClient.LOG_FILE_SIZE,1,true);
            handler.setLevel(ConfigClient.LOG_FILE_LEVEL);
            handler.setFormatter(new SimpleFormatter());
            Logger.getGlobal().addHandler(handler);
        } catch (IOException e) {
            log.log(Level.SEVERE,"log file '"+ConfigClient.LOG_FILE_NAME+"'not created :"+e);
        }

        configWindow();

        client = new Client();
    }

    public static void main(String[] args) {
        new Main();
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
        JButton b = new JButton("reg");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              b.setText(client.register("vasya","pass"));
            }
        });
        add(b);
        setVisible(true);
    }

}
