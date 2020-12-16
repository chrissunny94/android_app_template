package com.dewii.tracker.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public final class DateUtils {
    public static final String TAG = "DateUtils";

    //    public static final String FORMAT_DATE_N_TIME_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String FORMAT_DATE_N_TIME_ISO = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String FORMAT_DATE_DSMSY = "dd/MM/yyyy";
    public static final String FORMAT_DATE_YHMHD = "yyyy-MM-dd";
    public static final String FORMAT_DATE_YSMSD = "yyyy/MM/dd";

    private static final String FORMAT_TIME_24HMS = "HH:mm:ss";
    private static final String FORMAT_TIME_12HMA = "hh:mm a";
    private static final String FORMAT_TIME_12HMSA = "hh:mm:ss a";
    private static final String FORMAT_TIME_12HMS = "hh:mm:ss";

    private static final String FORMAT_DATE_TIME_YHMHD_24HMS = FORMAT_DATE_YHMHD + " " + FORMAT_TIME_24HMS;
    private static final String FORMAT_DATE_TIME_YHMHD_12HMA = FORMAT_DATE_YHMHD + " " + FORMAT_TIME_12HMA;
    private static final String FORMAT_DATE_TIME_YSMSD_24HMS = FORMAT_DATE_DSMSY + " " + FORMAT_TIME_24HMS;
    private static final String FORMAT_DATE_TIME_YSMSD_12HMA = FORMAT_DATE_DSMSY + " " + FORMAT_TIME_12HMA;
    private static final String FORMAT_DATE_TIME_YSMSD_12HMSA = FORMAT_DATE_DSMSY + " " + FORMAT_TIME_12HMSA;

    public static final String FORMAT_DEFAULT_UTC = FORMAT_DATE_TIME_YHMHD_24HMS;
    public static final String FORMAT_DEFAULT_UTC_TZ = FORMAT_DATE_TIME_YHMHD_24HMS;
    public static final String FORMAT_DEFAULT_LOCAL = FORMAT_DATE_TIME_YSMSD_12HMSA;

    public static final String FORMAT_SERVER_REQUISITION_ITEM = "dd MMM yy hh:mm a";
    public static final String FORMAT_SERVER_REQUISITION_VIEW = FORMAT_DATE_TIME_YHMHD_24HMS;

    public static final String FORMAT_SERVER_INDENT_VIEW = FORMAT_DATE_YHMHD + " " + FORMAT_TIME_12HMS;

    public static final String FORMAT_SERVER_DIESEL = FORMAT_DATE_TIME_YHMHD_24HMS;
    public static final String FORMAT_SERVER_PO = FORMAT_DATE_TIME_YHMHD_24HMS;

    private DateUtils() {
    }


    private static SimpleDateFormat utcSdf() {
        return utcSdf(FORMAT_DEFAULT_UTC);
    }

    private static SimpleDateFormat utcSdf(String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat;
    }

    private static SimpleDateFormat localSdf() {
        return localSdf(FORMAT_DEFAULT_LOCAL);
    }

    private static SimpleDateFormat localSdf(String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());

        return simpleDateFormat;
    }


    private static long toUtcMillis() throws Exception {
        return utcSdf().parse(toUtcString()).getTime();
    }

    private static long toUtcMillis(long timeStamp) throws Exception {
        return utcSdf().parse(toUtcString(timeStamp)).getTime();
    }

    private static long toUtcMillis(String localDateTime, String inPattern) throws Exception {
        return utcSdf(inPattern).parse(localDateTime).getTime();
    }


    private static String toUtcString() throws Exception {
        return utcSdf().format(new Date());
    }

    private static String toUtcString(String outPattern) throws Exception {
        return utcSdf(outPattern).format(new Date());
    }

    private static String toUtcString(long timeStamp) throws Exception {
        return utcSdf().format(new Date(timeStamp));
    }

    private static String toUtcString(long timeStamp, String outPattern) throws Exception {
        return utcSdf(outPattern).format(new Date(timeStamp));
    }

    private static String toUtcString(String localDateTime, String inPattern, String outPattern) throws Exception {
        return utcSdf(outPattern).format(utcSdf(inPattern).parse(localDateTime));
    }


    private static long toLocalMillis() throws Exception {
        return localSdf().parse(toUtcString()).getTime();
    }

    private static long toLocalMillis(long timeStamp) throws Exception {
        return localSdf().parse(toLocalString(timeStamp)).getTime();
    }

    private static long toLocalMillis(String unixDateTime, String inPattern) throws Exception {
        return 0;
    }


    private static String toLocalString() throws Exception {
        return localSdf().format(new Date());
    }

    private static String toLocalString(String outPattern) throws Exception {
        return localSdf(outPattern).format(new Date());
    }

    public static String toLocalString(long timeStamp) throws Exception {
        return localSdf().format(new Date(timeStamp));
    }

    public static String toLocalString(long timeStamp, String outPattern) throws Exception {
        return localSdf(outPattern).format(new Date(timeStamp));
    }

    private static String toLocalString(String unixDateTime, String inPattern, String outPattern) throws Exception {
        return localSdf(outPattern).format(localSdf(inPattern).parse(unixDateTime));
    }


    private static SimpleDateFormat sdfForFormat(String pattern) {
        return sdfForFormat(pattern, false);
    }

    private static SimpleDateFormat sdfForFormat(String pattern, boolean unix) {
        return sdfForFormat(pattern, unix, Locale.getDefault());
    }

    private static SimpleDateFormat sdfForFormat(String pattern, boolean unix, Locale locale) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
        if (unix) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return simpleDateFormat;
    }

    private static SimpleDateFormat getYSMSD12HMA() {
        return sdfForFormat(FORMAT_DATE_TIME_YSMSD_12HMA);
    }

    private static SimpleDateFormat getYSMSD12HMSA() {
        return sdfForFormat(FORMAT_DATE_TIME_YSMSD_12HMSA);
    }

    private static SimpleDateFormat getYSMSD24HMS() {
        return sdfForFormat(FORMAT_DATE_TIME_YSMSD_24HMS);
    }

    private static SimpleDateFormat getYHMHD24HMS() {
        return sdfForFormat(FORMAT_DATE_TIME_YHMHD_24HMS);
    }


    public static long toLocal(long millis) {
        return toLocal(false, millis);
    }

    public static long toLocal(boolean addTimeZone, long millis) {
        if (addTimeZone)
            return millis + (5 * 60 * 60 * 1000 /*5 hours*/ + 30 * 60 * 1000 /*30 minutes*/);
        else
            return millis * 1000L;
    }

    public static long localMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }


    public static long toUnix(long millis) {
        return millis / 1000L;
    }

    public static long unixMillis() {
        return toUnix(Calendar.getInstance().getTimeInMillis());
    }

    public static long toUnixMillis(String localDate) throws ParseException {
        return toUnix(getYHMHD24HMS().parse(localDate).getTime());
    }


    public static String toFormat(String format, boolean unix, long millis) {
        SimpleDateFormat sdf = sdfForFormat(format);
        if (unix)
            sdf.setTimeZone(new SimpleTimeZone(0, "UTC"));

        return sdf.format(new Date(millis));
    }

    public static String toPattern(String date, String fromPattern, String toPattern) throws ParseException {
        return sdfForFormat(toPattern).format(sdfForFormat(fromPattern).parse(date));
    }


    public static String today() {
        return today(FORMAT_DATE_DSMSY);
    }

    public static String today(String format) {
        return today(format, false);
    }

    public static String today(String format, boolean unix) {
        SimpleDateFormat sdf = sdfForFormat(format);
        if (unix)
            sdf.setTimeZone(new SimpleTimeZone(0, "UTC"));

        return sdf.format(new Date());
    }


    public static String now() {
        return now(FORMAT_TIME_12HMSA, false, 0);
    }

    public static String now(String format) {
        return now(format, false, 0);
    }

    public static String now(String format, int addend) {
        return now(format, false, addend);
    }

    public static String now(String format, boolean unix) {
        return now(format, unix, 0);
    }

    public static String now(String format, boolean unix, int addend) {
        SimpleDateFormat sdf = sdfForFormat(format);
        if (unix)
            sdf.setTimeZone(new SimpleTimeZone(0, "UTC"));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, addend);
        return sdf.format(calendar.getTime());
    }


    public static String todaynow() {
        return today(FORMAT_DEFAULT_LOCAL);
    }

    public static String todaynow(String format) {
        return todaynow(format, false);
    }

    public static String todaynow(String format, boolean unix) {
        SimpleDateFormat sdf = sdfForFormat(format);
        if (unix)
            sdf.setTimeZone(new SimpleTimeZone(0, "UTC"));

        return sdf.format(new Date());
    }

    public static long toRequisitionServerMillis(String serverDateTime) throws Exception {
        return sdfForFormat(FORMAT_SERVER_REQUISITION_ITEM, true, Locale.ENGLISH).parse(serverDateTime).getTime();
    }

    public static long toRequisitionLocalMillis(String serverDateTime) throws Exception {
        return toLocal(true, toRequisitionServerMillis(serverDateTime));
    }

    public static String toRequisitionLocalString(String serverDateTime) throws Exception {
        return sdfForFormat(FORMAT_DEFAULT_LOCAL).format(new Date(toRequisitionLocalMillis(serverDateTime)));
    }

    public static String toRequisitionLocalString(String serverDateTime, String inPattern) throws Exception {
        return localSdf().format(new Date(utcSdf(inPattern).parse(serverDateTime).getTime()));
    }


    public static String toIndentLocalString(String serverDateTime, String inPattern) throws Exception {
        return localSdf().format(new Date(utcSdf(inPattern).parse(serverDateTime).getTime()));
    }


    public static String getDateInString(Date date) {
        return sdfForFormat(FORMAT_DATE_YHMHD).format(date);
    }

    public static String getTimeInString(Date date) {
        return sdfForFormat(FORMAT_TIME_12HMA).format(date);
    }


    public static String getFilterDate(boolean forRequest, long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return sdfForFormat(forRequest ? FORMAT_DATE_YHMHD
                : FORMAT_DATE_TIME_YSMSD_12HMA).format(calendar.getTime());
    }

    public static String fileTimeStamp() {
        String todayNow = todaynow(FORMAT_DATE_TIME_YSMSD_24HMS);
        todayNow = todayNow.replace("/", "");
        todayNow = todayNow.replace(" ", "_");
        todayNow = todayNow.replace(":", "");

        return todayNow;
    }

    public static String getFormattedDate(String date, String inputFormat, String outputFormat) {
        if (date == null || date.isEmpty() || date.equalsIgnoreCase("null")) {
            return "NA";
        }
        try {
            SimpleDateFormat inputSdf = new SimpleDateFormat(inputFormat);
            Date mDate = inputSdf.parse(date);
            SimpleDateFormat outputSdf = new SimpleDateFormat(outputFormat);
            return outputSdf.format(mDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }

    public static String getUTCTime(String date, String inputFormat, String outputFormat) {
        try {
            if (date == null || date.isEmpty() || date.equalsIgnoreCase("null")) {
                return "NA";
            } else {
                SimpleDateFormat format = new SimpleDateFormat(inputFormat);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date inTime = format.parse(date);
                return (String) DateFormat.format(outputFormat, inTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }

    public static String formatToYesterdayOrToday(String date) {
        try {
            Date dateTime = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return "Today";
            } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                return "Yesterday";
            } else {
                return new SimpleDateFormat("dd-MM-yyyy").format(dateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTSZString() {
        return getTSZString(System.currentTimeMillis());
    }

    public static String getTSZString(long millis) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(new Date(millis));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
