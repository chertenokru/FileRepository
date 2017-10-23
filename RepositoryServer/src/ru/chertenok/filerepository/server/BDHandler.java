package ru.chertenok.filerepository.server;

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

      BDHandler.connectionStr = connectionStr;

            //connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
            // stmt = connection.createStatement();




    }

    public static void close() {

    }


    public static void registerUser(String userName,String userPassword) throws Exception
    {

    }

    public static void loginUser(String userName,String userPassword) throws Exception
    {

    }

    public static void addUserFile(String userName,String serverFileName,String clientFileName)
    {

    }




}
