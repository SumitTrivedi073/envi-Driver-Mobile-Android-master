package sgs.env.ecabsdriver.util;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    private static final String TAG = "DateHelper";
    private static final SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d yyyy, HH:mm:ss", Locale.getDefault());

    public static String getFormattedDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat output = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
     /*   SimpleDateFormat output = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm ");*/
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//        sdf.setTimeZone(TimeZone.getDefault());

        Date d = sdf.parse(dateString);
        String formattedTime = output.format(d);

        Date calDate = sdf.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calDate);
        calendar.add(Calendar.HOUR, 5);
        calendar.add(Calendar.MINUTE, 30);

        Date convertedDate = calendar.getTime();
        String convTime = output.format(convertedDate);

        return convTime;
    }


    public static int isSameDay(Date todaysDate, String firebaseCreatedDate)  {

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        String today = fmt.format(todaysDate);
        Date dateFirebaseCreatedDate = null;
        Date dateTodaysDate = null;
        try {
            dateFirebaseCreatedDate = fmt.parse(firebaseCreatedDate);
            dateTodaysDate = fmt.parse(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTodaysDate.compareTo(dateFirebaseCreatedDate);
    }

    public static int getYear(Date d){
        int year=d.getYear();
        int currentYear=year+1900;
        return currentYear;
    }
    public static int getMonth(Date d){
        int month=d.getMonth();
        return month;
    }
    public static int getDate(Date d){
        int date=d.getDate();
        return date;
    }
}
