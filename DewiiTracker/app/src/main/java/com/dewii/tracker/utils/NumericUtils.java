package com.dewii.tracker.utils;

import android.os.Build;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class NumericUtils {
    private static final int INT_RANDOM_START_FOR_DIESEL = 999999999;

    public interface OnRandomIntegerListener {
        void onRandomIntGenerated(long integer);
    }

    private NumericUtils() {

    }



    public static boolean isZeroF(EditText editText) {
        return parseFloat(editText.getText().toString()) == 0;
    }

    public static boolean isZeroI(EditText editText) {
        return parseInt(editText.getText().toString()) == 0;
    }

    public static boolean isZeroL(EditText editText) {
        return parseLong(editText.getText().toString()) == 0;
    }

    public static boolean isZeroD(EditText editText) {
        return parseDouble(editText.getText().toString()) == 0;
    }



    public static boolean isZeroF(String value) {
        return parseFloat(value) == 0;
    }

    public static boolean isZeroI(String value) {
        return parseInt(value) == 0;
    }

    public static boolean isZeroL(String value) {
        return parseLong(value) == 0;
    }

    public static boolean isZeroD(String value) {
        return parseDouble(value) == 0;
    }



    public static float parseFloat(EditText editText) {
        return parseFloat(editText.getText().toString().trim());
    }

    public static long parseLong(EditText editText) {
        return parseLong(editText.getText().toString().trim());
    }

    public static double parseDouble(EditText editText) {
        return parseDouble(editText.getText().toString().trim());
    }

    public static int parseInt(EditText editText) {
        return parseInt(editText.getText().toString().trim());
    }



    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            e.printStackTrace();
            return (float) 0;
        }
    }

    public static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            e.printStackTrace();
            return (long) 0;
        }
    }

    public static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            e.printStackTrace();
            return (double) 0;
        }
    }



    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }



    public static boolean toBoolean(int value) {
        return value == 1;
    }

    public static int toInt(boolean value) {
        return value ? 1 : 0;
    }



    public static boolean isGreaterInDoubles(String s1, String s2) {
        return isGreater(parseDouble(s1), parseDouble(s2));
    }

    public static boolean isGreaterOrEqualInDoubles(String s1, String s2) {
        return isGreaterOrEqual(parseDouble(s1), parseDouble(s2));
    }

    public static boolean isGreater(double d1, double d2) {
        return (d1 > d2);
    }

    public static boolean isGreaterOrEqual(double d1, double d2) {
        return (d1 >= d2);
    }



    public static boolean areEqualDoubles(String s1, String s2) {
        return areEqual(parseDouble(s1), parseDouble(s2));
    }

    public static boolean areEqual(double d1, double d2) {
        return d1 == d2;
    }



    public static boolean isInvalidDouble(Double d) {
        return d == null || d.isNaN() || d.isInfinite();
    }

    public static double makeNonNaN(double value) {
        if (Double.valueOf(value).isNaN())
            return 0;
        else
            return value;
    }



    public static int parseVersionInt(String[] versionCode, int position) {
        String code;
        if (position >= versionCode.length)
            code = "0";
        else
            code = versionCode[position];

        return parseInt(code);
    }



    public static double roundOff(double num)
    {
        return Math.round(num * 100.0) / 100.0;
    }

    public static int getRandomInt(int countRandomsUsed) {
        return INT_RANDOM_START_FOR_DIESEL - countRandomsUsed;
    }

    public static long getRandomInt(List<Long> excludes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return randomGeneratorFor21Above(excludes);
        } else {
            return randomGeneratorFor21AndBelow(excludes);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static long randomGeneratorFor21Above(List<Long> excludes) {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        long value = threadLocalRandom.nextLong(100000, 10000000);
        if (excludes.contains(value) || value < 0)
            return getRandomInt(excludes);
        else
            return value;
    }

    private static long randomGeneratorFor21AndBelow(List<Long> excludes) {
        Random random = new Random();
        long value = random.nextLong();
        if (excludes.contains(value) || value < 0)
            return getRandomInt(excludes);
        else {
            String valueAsString = String.valueOf(value);
            if (valueAsString.length() > 8) {
                return value / (long) Math.pow(10, valueAsString.length() - 8);
            }
            return value;
        }
    }
}
