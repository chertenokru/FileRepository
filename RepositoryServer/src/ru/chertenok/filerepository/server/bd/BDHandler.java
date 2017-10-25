package ru.chertenok.filerepository.server.bd;

import ru.chertenok.filerepository.server.utils.Utils;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.chertenok.filerepository.server.utils.Utils.getHashCode;


public class BDHandler {
    private static Logger log = Logger.getGlobal();
    private static String connectionStr;
    private static Connection connection;


    private BDHandler() {
    }

    public static void init(String connectionStr, String nameBD) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        BDHandler.connectionStr = connectionStr;

        connection = DriverManager.getConnection(String.format(connectionStr, nameBD));
        log.log(Level.INFO, String.format(connectionStr, nameBD));
    }

    public static void close() throws SQLException {
        connection.close();

    }


    public static void registerUser(String userName, String userPassword) throws SQLException {

        PreparedStatement st = connection.prepareStatement("insert INTO users (login,password) VALUES (?,?)");
        st.setString(1, userName.trim());
        st.setString(2, getHashCode(userPassword.trim(), Utils.HashCode.SH256));
        st.execute();
        st.close();


    }

    public static boolean loginUser(String userName, String userPassword) throws Exception {
        boolean result = false;
        log.log(Level.INFO, "login userName = " + userName);
        PreparedStatement st = connection.prepareStatement("select count(*) from users where login = ? and password = ?");
        st.setString(1, userName.trim());
        st.setString(2, getHashCode(userPassword.trim(), Utils.HashCode.SH256));
        ResultSet rs = st.executeQuery();
        if (rs != null) {
            if (rs.getInt(1) != 0) {
                result = true;
            }
            rs.close();
        }
        st.close();
        return result;

    }

    public static void addUserFile(String userName, String serverFileName, String clientFileName) {

    }


    public static boolean checkName(String userLogin) throws SQLException {
        boolean result = false;
        log.log(Level.INFO, "check userName = " + userLogin);
        PreparedStatement st = connection.prepareStatement("select count(*) from users where login = ?");
        st.setString(1, userLogin.trim());
        ResultSet rs = st.executeQuery();
        if (rs != null) {
            if (rs.getInt(1) == 0) {
                result = true;
            }
            rs.close();
        }
        st.close();
        return result;
    }
}
