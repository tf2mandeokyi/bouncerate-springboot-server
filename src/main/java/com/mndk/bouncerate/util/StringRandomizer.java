package com.mndk.bouncerate.util;

import java.util.Random;

public class StringRandomizer {

    private static final Random RANDOM = new Random();

    private static final String AZaz09 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static String nextAZaz09String(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++) stringBuilder.append(getRandomChar(AZaz09));
        return stringBuilder.toString();
    }

    private static char getRandomChar(String charList) {
        return charList.charAt(RANDOM.nextInt(charList.length()));
    }

}
