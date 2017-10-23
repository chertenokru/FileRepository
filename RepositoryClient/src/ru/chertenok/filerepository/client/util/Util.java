package ru.chertenok.filerepository.client.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    public  enum HashCode {
            MD5("MD5"), SH1("SHA-1"), SH256("SHA-256"),SHA512("SHA-512"),RipeMD("RipeMD") ;
        private final String code;
        private HashCode(String s) {
            code = s;
       }
       public String getName(){
            return code;
       }
    }




    public static String getHashCode(String st,HashCode code ) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance(code.getName());
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            // тут можно обработать ошибку
            // возникает она если в передаваемый алгоритм в getInstance(,,,) не существует
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while( md5Hex.length() < 32 ){
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }
}
