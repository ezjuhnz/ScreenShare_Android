package com.cmbc.av.utils;

public class StringUtils {
    public static boolean isEmpty(String s){
        if (s == null || s.trim().length() == 0){
            return true;
        }
        return false;
    }
}
