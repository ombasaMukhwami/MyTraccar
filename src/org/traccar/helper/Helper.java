package org.traccar.helper;

public final class Helper {

    private Helper() {
    }
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
        StringBuilder binaryResult = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            binaryResult.append(numVal % 2);
            numVal = numVal / 2;
        }
        return binaryResult.reverse().toString();

    }
    public static String toBinary(int numVal, int bit) {
        // Declare a few variables we're going to need
        StringBuilder binaryResult = new StringBuilder();
        for (int i = 0; i < bit; i++) {
            binaryResult.append(numVal % 2);
            numVal = numVal / 2;
        }
        return binaryResult.reverse().toString();

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

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i),  16) << 4) + Character.digit(s.charAt(i + 1),  16));
        }
        return data;
    }
    public static byte[] hexStringToByteArrayTest(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index,  index + 2),  16);
            b[i] = (byte) v;
        }
        return b;
    }
    public static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public  static  String byteToHex(int b) {
        int i = b & 0XFF;
        //return String.format("%02s",  Integer.toHexString(i));
        return String.format("%02x", i);
    }

    public static String getTimeString(int nDateTime) {
        int ndate = 0;
        StringBuilder sb = new StringBuilder();
        ndate = nDateTime >> 26; //year
        sb.append((ndate / 10));
        sb.append((ndate % 10) + "-");
        ndate = nDateTime >> 22 & 0x0f; //month f=15
        sb.append((ndate / 10));
        sb.append((ndate % 10) + "-");
        ndate = nDateTime >> 17 & 0x1f; //day 2f=31
        sb.append((ndate / 10));
        sb.append((ndate % 10) + " ");
        ndate = nDateTime >> 12 & 0x1f; //hour 3f=63
        sb.append((ndate / 10));
        sb.append((ndate % 10) + ":");
        ndate = nDateTime >> 6 & 0x3f; //minute
        sb.append((ndate / 10));
        sb.append((ndate % 10) + ":");
        ndate = nDateTime & 0x3f; //second
        sb.append((ndate / 10));
        sb.append((ndate % 10));

        return sb.toString();
    }
}
