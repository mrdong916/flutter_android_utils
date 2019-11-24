package com.bzqll.flutter_android_utils;

import java.io.UnsupportedEncodingException;

public final class KWBase64Util {

    private static char[] map1 = new char[64];
    private static byte[] map2 = new byte[128];

    static {
        int i = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            map1[i++] = c;
        }
        for (char c = 'a'; c <= 'z'; c++) {
            map1[i++] = c;
        }
        for (char c = '0'; c <= '9'; c++) {
            map1[i++] = c;
        }
        map1[i++] = '+';
        map1[i] = '/';
    }

    static {
        for (int i = 0; i < map2.length; i++) {
            map2[i] = -1;
        }
        for (int i = 0; i < 64; i++) {
            map2[map1[i]] = (byte) i;
        }
    }

    /*
     * ************************************** encode *********************************************
     */

    private KWBase64Util() {
    }

    /**
     * Encodes a string into Base64 format. No blanks or line breaks are inserted.
     *
     * @param s a String to be encoded.
     * @return A String with the Base64 encoded data.
     */
    public static String encodeString(String s) {
        return encode(s.getBytes());
    }

    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are inserted.
     *
     * @param in an array containing the data bytes to be encoded.
     * @return A character array with the Base64 encoded data.
     */
    public static String encode(byte[] in) {
        return new String(encode(in, in.length));
    }

    /**
     * Encodes a string into Base64 format. No blanks or line breaks are inserted.
     *
     * @param s a String to be encoded.
     * @return A String with the Base64 encoded data.
     */
    public static String encodeString(String s, String charset, String key) {
        String result = "";
        try {
            result = new String(encode(s.getBytes(charset), s.getBytes(charset).length, key));
        } catch (Exception e) {
        }
        return result;

    }

    public static char[] encode(byte[] in, int iLen) {
        return encode(in, iLen, null);
    }

    /*
     * ********************** decode ******************************************
     */

    /**
     * Encodes a byte array into Base64 format. No blanks or line breaks are inserted.
     *
     * @param in   an array containing the data bytes to be encoded.
     * @param iLen number of bytes to process in <code>in</code>.
     * @return A character array with the Base64 encoded data.
     */
    public static char[] encode(byte[] in, int iLen, String key) {

        // 如果key不为空，则按位与key异或
        if (key != null && !key.equals("")) {
            byte[] keyArr = key.getBytes();
            for (int i = 0; i < in.length; ) {
                for (int j = 0; j < keyArr.length && i < in.length; j++) {
                    in[i++] ^= keyArr[j];
                }
            }
        }

        int oDataLen = (iLen * 4 + 2) / 3;
        int oLen = ((iLen + 2) / 3) * 4;
        char[] out = new char[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++] & 0xff;
            int i1 = ip < iLen ? in[ip++] & 0xff : 0;
            int i2 = ip < iLen ? in[ip++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 = i2 & 0x3F;
            out[op++] = map1[o0];
            out[op++] = map1[o1];
            out[op] = op < oDataLen ? map1[o2] : '=';
            op++;
            out[op] = op < oDataLen ? map1[o3] : '=';
            op++;
        }
        return out;
    }

    public static String decodeString(String s, String charset, String key) {
        byte[] result = decode(s.toCharArray());
        byte[] keyArr = key.getBytes();
        for (int i = 0; i < result.length; ) {
            for (int j = 0; j < keyArr.length && i < result.length; j++) {
                result[i++] ^= keyArr[j];
            }
        }
        try {
            if (charset == null) {
                return new String(result);
            } else {
                return new String(result, charset);
            }
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    /**
     * Decodes a string from Base64 format.
     *
     * @param s a Base64 String to be decoded.
     * @return A String containing the decoded data.
     * @throws UnsupportedEncodingException
     * @throws IllegalArgumentException     if the input is not valid Base64 encoded data.
     */
    public static String decodeString(String s, String charset) throws UnsupportedEncodingException {
        return new String(decode(s), charset);
    }

    /**
     * Decodes a byte array from Base64 format.
     *
     * @param s a Base64 String to be decoded.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException if the input is not valid Base64 encoded data.
     */
    public static byte[] decode(String s) {
        // s=discardNonBase64Chars(s);
        return decode(s.toCharArray());
    }

    /**
     * Decodes a byte array from Base64 format. No blanks or line breaks are allowed within the Base64 encoded data.
     *
     * @param in a character array containing the Base64 encoded data.
     * @return An array containing the decoded data bytes.
     * @throws IllegalArgumentException if the input is not valid Base64 encoded data.
     */
    public static byte[] decode(char[] in) {
        int iLen = in.length;
        if (iLen % 4 != 0) {
            throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
        }
        while (iLen > 0 && in[iLen - 1] == '=') {
            iLen--;
        }
        int oLen = (iLen * 3) / 4;
        byte[] out = new byte[oLen];
        int ip = 0;
        int op = 0;
        while (ip < iLen) {
            int i0 = in[ip++];
            int i1 = in[ip++];
            int i2 = ip < iLen ? in[ip++] : 'A';
            int i3 = ip < iLen ? in[ip++] : 'A';
            if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127) {
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }
            int b0 = map2[i0];
            int b1 = map2[i1];
            int b2 = map2[i2];
            int b3 = map2[i3];
            if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0) {
                throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
            }
            int o0 = (b0 << 2) | (b1 >>> 4);
            int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
            int o2 = ((b2 & 3) << 6) | b3;
            out[op++] = (byte) o0;
            if (op < oLen) {
                out[op++] = (byte) o1;
            }
            if (op < oLen) {
                out[op++] = (byte) o2;
            }
        }
        return out;
    }

}