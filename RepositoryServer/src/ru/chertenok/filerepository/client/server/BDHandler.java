package ru.chertenok.filerepository.client.server;

import org.sqlite.SQLiteConfig;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.util.logging.Logger;


public class BDHandler {
    private static Logger log = Logger.getGlobal();
    private static String connectionStr;
    private static SQLiteConnectionPoolDataSource datesoure;
    private static SQLiteConfig sqlConfig;


    private BDHandler() {
    }

    public static void init( String connectionStr, String nameBD) {

//        try {
//            Class.forName(bdDriverName);
//        } catch (ClassNotFoundException e) {
//            log.log(Level.SEVERE,"Ошибка подключения драйвера БД: "+e);
//        }
//
      BDHandler.connectionStr = connectionStr;

      sqlConfig = new SQLiteConfig();
      sqlConfig.enforceForeignKeys(true);
      sqlConfig.enableLoadExtension(true);
      datesoure = new SQLiteConnectionPoolDataSource(sqlConfig);
      datesoure.setUrl(String.format(connectionStr,nameBD));


            //connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
            // stmt = connection.createStatement();




    }

    public static void close() {

    }

}
