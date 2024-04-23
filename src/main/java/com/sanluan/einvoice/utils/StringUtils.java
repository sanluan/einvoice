package com.sanluan.einvoice.utils;

public class StringUtils {

    public static String replace(String str) {
        return str.replaceAll(" ", "").replaceAll("　", "").replaceAll("：", ":").replaceAll(" ", "");
    }
}
