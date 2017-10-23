package ru.chertenok.filerepository.client;

import ru.chertenok.filerepository.client.util.Util;

public class Main {

    public static void main(String[] args) {
        System.out.println(Util.getHashCode("rrgrgrgrgr", Util.HashCode.MD5));
        System.out.println(Util.getHashCode("rrgrgrgrgr", Util.HashCode.SH1));
        System.out.println(Util.getHashCode("rrgrgrgrgr", Util.HashCode.SH256));
        System.out.println(Util.getHashCode("rrgrgrgrgr", Util.HashCode.SHA512));
    }
}
