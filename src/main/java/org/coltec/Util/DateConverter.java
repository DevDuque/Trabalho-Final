package org.coltec.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static Date stringToDate(String dateString) throws ParseException {
        return sdf.parse(dateString);
    }

    public static String dateToString(Date date) {
        return sdf.format(date);
    }

    public static boolean isDateEqualOrAfter(Date date1, Date date2) {
        String strDate1 = dateToString(date1);
        String strDate2 = dateToString(date2);
        return strDate1.compareTo(strDate2) >= 0;
    }

    public static boolean isDateEqualOrBefore(Date date1, Date date2) {
        String strDate1 = dateToString(date1);
        String strDate2 = dateToString(date2);
        return strDate1.compareTo(strDate2) <= 0;
    }
}
