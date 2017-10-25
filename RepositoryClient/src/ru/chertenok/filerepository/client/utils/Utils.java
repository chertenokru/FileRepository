package ru.chertenok.filerepository.client.utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class Utils {

    public static Border getBorderWithTitle(String title) {
        return
        BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                        title),
                BorderFactory.createEmptyBorder(30, 30, 30, 30));
    }
}
