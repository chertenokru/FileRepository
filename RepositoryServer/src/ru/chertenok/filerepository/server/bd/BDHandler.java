package ru.chertenok.filerepository.server.bd;

import java.sql.*;
import java.util.logging.Logger;


public class BDHandler {
    private static Logger log = Logger.getGlobal();
    private static String connectionStr;
    private static Connection connection;



    private BDHandler() {
    }

    public static void init( String connectionStr, String nameBD) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
      BDHandler.connectionStr = connectionStr;

            connection = DriverManager.getConnection(String.format(connectionStr,nameBD));
        System.out.println(String.format(connectionStr,nameBD));



            // stmt = connection.createStatement();




    }

    public static void close() throws SQLException {
        connection.close();

    }


    public static void registerUser(String userName,String userPassword) throws SQLException {
        PreparedStatement st = connection.prepareStatement("insert INTO users (login,password) VALUES (?,?)");
        st.setString(1, userName);
        st.setString(2,userPassword);
        st.execute();
        st.close();


    }

    public static void loginUser(String userName,String userPassword) throws Exception
    {

    }

    public static void addUserFile(String userName,String serverFileName,String clientFileName)
    {

    }




}
