package org.dynadroid.utils;

public class Debug {
    public static boolean debug = true;

    public static void println(String string) {
        if (debug) {
            System.out.println(string);
        }
    }

    public static void printlnForce(String string) {
        System.out.println(string);
    }

}
