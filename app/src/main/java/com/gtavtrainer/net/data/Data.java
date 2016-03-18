package com.gtavtrainer.net.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manoj Khanna
 */

public class Data {

    private final LinkedHashMap<String, String> mMap = new LinkedHashMap<>();

    public Data() {
    }

    public Data(String data) {
        Pattern pattern = Pattern.compile("([a-z_0-9]+) = (.+)");
        for (int i = 0; i < data.length(); i++) {
            char c1 = data.charAt(i);
            if (c1 != ' ' && c1 != '(' && c1 != ')' && c1 != ',' && c1 != '=') {
                for (int j = i + 1, p = 0; j < data.length(); j++) {
                    char c2 = data.charAt(j);
                    if (c2 == ',' || j == data.length() - 1) {
                        if (p == 0) {
                            Matcher matcher = pattern.matcher(data.substring(i, j));
                            if (!matcher.find()) {
                                throw new IllegalArgumentException();
                            }

                            mMap.put(matcher.group(1), matcher.group(2));

                            i = j;
                            break;
                        }
                    } else if (c2 == '(') {
                        p++;
                    } else if (c2 == ')') {
                        p--;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");

        for (Map.Entry<String, String> entry : mMap.entrySet()) {
            if (stringBuilder.length() > 1) {
                stringBuilder.append(", ");
            }

            stringBuilder.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue());
        }

        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    public Data putBoolean(String key, boolean value) {
        mMap.put(key, Boolean.toString(value));

        return this;
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(mMap.get(key));
    }

    public Data putByte(String key, byte value) {
        mMap.put(key, Byte.toString(value));

        return this;
    }

    public byte getByte(String key) {
        return Byte.parseByte(mMap.get(key));
    }

    public Data putChar(String key, char value) {
        mMap.put(key, Character.toString(value));

        return this;
    }

    public char getChar(String key) {
        return mMap.get(key).charAt(0);
    }

    public Data putShort(String key, short value) {
        mMap.put(key, Short.toString(value));

        return this;
    }

    public short getShort(String key) {
        return Short.parseShort(mMap.get(key));
    }

    public Data putInt(String key, int value) {
        mMap.put(key, Integer.toString(value));

        return this;
    }

    public int getInt(String key) {
        return Integer.parseInt(mMap.get(key));
    }

    public Data putLong(String key, long value) {
        mMap.put(key, Long.toString(value));

        return this;
    }

    public long getLong(String key) {
        return Long.parseLong(mMap.get(key));
    }

    public Data putFloat(String key, float value) {
        mMap.put(key, Float.toString(value));

        return this;
    }

    public float getFloat(String key) {
        return Float.parseFloat(mMap.get(key));
    }

    public Data putDouble(String key, double value) {
        mMap.put(key, Double.toString(value));

        return this;
    }

    public double getDouble(String key) {
        return Double.parseDouble(mMap.get(key));
    }

    public Data putString(String key, String value) {
        mMap.put(key, value);

        return this;
    }

    public String getString(String key) {
        return mMap.get(key);
    }

    public Data putData(String key, Data value) {
        mMap.put(key, value.toString());

        return this;
    }

    public Data getData(String key) {
        return new Data(mMap.get(key));
    }

    public boolean contains(String key) {
        return mMap.containsKey(key);
    }

}
