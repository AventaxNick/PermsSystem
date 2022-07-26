package de.aventax.utils;

import java.util.Random;

public class CodeGenerator {

    private static final Random random = new Random();
    private static final String[] keys = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    private static String getOneCode() {
        int min = 1;
        int max = keys.length;
        int rnd = random.nextInt(max + 1 - min) + min;
        return keys[rnd];
    }

    public static String getFullCode() {

        String s = getOneCode();

        int min = 6;
        int max = 10;

        Random random = new Random();
        int lines = random.nextInt(max + 1 - min) + min;

        for (int i = 1; i < lines; i++) {
            s = s + getOneCode();
        }
        return s;
    }

}
