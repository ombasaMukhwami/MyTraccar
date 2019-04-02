package org.traccar.helper;

public class Helper {

    public static String toBinaryx(int no) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (no > 0) {
            result.append(no % 2);
            i++;
            no = no / 2;
        }
        result.reverse();
        return result.toString();
    }

    public static String toBinary(int numVal) {
        // Declare a few variables we're going to need
        StringBuilder BinaryResult = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            BinaryResult.append(numVal % 2);
            numVal = numVal / 2;
        }
        return BinaryResult.reverse().toString();

    }
    public static int toInteger(String s) {
        int x = 128;
        int total = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                total = total + (x * 1);
            }
            x = x / 2;
        }

        return  total;
    }
}
