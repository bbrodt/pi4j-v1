package com.pi4j.util;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  StringUtil.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2024 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


public class StringUtil {

    public static final String EMPTY = "";
    public static final char DEFAULT_PAD_CHAR = ' ';

    public static boolean isNullOrEmpty(String data, boolean trim){
        if(data == null)
            return true;

        // trim if requested
        String test = data;
        if(trim)
            test = data.trim();

        return (test.length() <= 0);
    }

    public static boolean isNullOrEmpty(String data){
        return isNullOrEmpty(data, false);
    }

    public static boolean isNotNullOrEmpty(String data){
        return isNotNullOrEmpty(data, false);
    }

    public static boolean isNotNullOrEmpty(String data, boolean trim){
        return !(isNullOrEmpty(data, trim));
    }

    public static boolean contains(String source, String target)  {
        return (null != source && null != target && source.contains(target));
    }

    public static boolean contains(String source, String[] targets)  {
        if (null != source && null != targets) {
            for(String target : targets) {
                if (source.contains(target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean contains(String[] sources, String target)  {
        if (null != sources && null != target) {
            for (String source : sources) {
                if(contains(source, target))
                    return true;
            }
        }
        return false;
    }

    public static boolean contains(String[] sources, String[] targets)  {
        if (null != sources && null != targets) {
            for (String source : sources) {
                if(contains(source, targets))
                    return true;
            }
        }
        return false;
    }

    public static String create(int length)  {
        return create(DEFAULT_PAD_CHAR, length);
    }

    public static String create(char c, int length)  {
        StringBuilder sb = new StringBuilder(length);
        for(int index = 0; index < length; index++)
            sb.append(c);
        return sb.toString();
    }

    public static String create(String s, int length)  {
        StringBuilder sb = new StringBuilder(length * s.length());
        for(int index = 0; index < length; index++)
            sb.append(s);
        return sb.toString();
    }

    public static String repeat(char c, int length)  {
        return create(c, length);
    }

    public static String padLeft(String data, int length)  {
        return padLeft(data, DEFAULT_PAD_CHAR, length);
    }

    public static String padLeft(String data, char pad, int length)  {
        StringBuilder sb = new StringBuilder(data.length() + length);
        for(int index = 0; index < length; index++)
            sb.append(pad);
        sb.append(data);
        return sb.toString();
    }

    public static String padLeft(String data, String pad, int length)  {
        StringBuilder sb = new StringBuilder(data.length() + (length * pad.length()));
        for(int index = 0; index < length; index++)
            sb.append(pad);
        sb.append(data);
        return sb.toString();
    }

    public static String padRight(String data, int length)  {
        return padRight(data, DEFAULT_PAD_CHAR, length);
    }

    public static String padRight(String data, char pad, int length)  {
        StringBuilder sb = new StringBuilder(data.length() + length);
        sb.append(data);
        for(int index = 0; index < length; index++)
            sb.append(pad);
        return sb.toString();
    }

    public static String padRight(String data, String pad, int length)  {
        StringBuilder sb = new StringBuilder(data.length() + (length * pad.length()));
        sb.append(data);
        for(int index = 0; index < length; index++)
            sb.append(pad);
        return sb.toString();
    }

    public static String pad(String data, int length)  {
        return pad(data, DEFAULT_PAD_CHAR, length);
    }

    public static String pad(String data, char pad, int length)  {
        return create(pad, length) + data + create(pad, length);
    }

    public static String pad(String data, String pad, int length)  {
        return create(pad, length) + data + create(pad, length);
    }

    public static String padCenter(String data, int length) {
        return padCenter(data, DEFAULT_PAD_CHAR, length);
    }

    public static String padCenter(String data, char pad, int length) {
        if(data.length() < length) {
            int needed = length - data.length();
            int padNeeded = needed / 2;
            StringBuilder result = new StringBuilder();
            result.append(create(pad, padNeeded));
            result.append(data);
            result.append(create(pad, padNeeded));
            int remaining = length - result.length();
            result.append(create(pad, remaining));
            return result.toString();
        }
        return data;
    }

    public static String trimLeft(String data)  {
        return trimLeft(data, DEFAULT_PAD_CHAR);
    }

    public static String trimLeft(String data, char trim)  {
        for(int index = 0; index < data.length(); index++)
            if(!(data.charAt(index) == trim))
                return data.substring(index);
        return EMPTY;
    }

    public static String trimRight(String data)  {
        return trimRight(data, DEFAULT_PAD_CHAR);
    }

    public static String trimRight(String data, char trim)  {
        int count = 0;
        for(int index = data.length(); index > 0; index--)
            if(data.charAt(index-1) == trim)
                count++;
            else
                return data.substring(0, data.length() - count);
        return EMPTY;
    }

    public static String trim(String data)  {
        return trim(data, DEFAULT_PAD_CHAR);
    }

    public static String trim(String data, char trim)  {
        String result = trimLeft(data, trim);
        return trimRight(result, trim);
    }

    public static String center(String text, int length){
        String out = String.format("%"+length+"s%s%"+length+"s", "",text,"");
        float mid = (out.length()/2);
        float start = mid - (length/2);
        float end = start + length;
        return out.substring((int) start, (int) end);
    }

    public static String concat(String ... data)  {
        StringBuilder sb = new StringBuilder();
        for(String d : data){
            sb.append(d);
        }
        return sb.toString();
    }

    public static String byteArrayToHex(byte[] data) {
        return byteArrayToHex(data, ",");
    }

    public static String byteArrayToHex(byte[] data, CharSequence delimiter) {
        return byteArrayToHex(data, delimiter, "0x");
    }

    public static String byteArrayToHex(byte[] data, CharSequence delimiter, CharSequence prefix) {

        // calculate the size needed for the string builder and create a new string builder
        int length = (data.length * 2) + (data.length * delimiter.length()) + (data.length * prefix.length());
        StringBuilder sb = new StringBuilder(length);

        // determine if delimiter and prefix are present
        boolean has_delimiter = delimiter.length() > 0;
        boolean has_prefix = prefix.length() > 0;
        int byte_count = 0;

        // iterate over all the bytes in the byte array
        for(byte b: data) {
            // append delimiter if previous bytes have already been included in the string builder
            if(has_delimiter && byte_count > 0) sb.append(delimiter);

            // append prefix
            if(has_prefix) sb.append(prefix);

            // append HEX representation of byte
            sb.append(String.format("%02X", b));

            // increment byte count
            byte_count++;
        }

        // return HEX string
        return sb.toString();
    }
}
