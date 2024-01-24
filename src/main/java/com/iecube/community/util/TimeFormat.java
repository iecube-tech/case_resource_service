package com.iecube.community.util;

import java.text.SimpleDateFormat;
import java.util.Date;
public class TimeFormat {
    public static String timeFormat(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH点mm分");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }
}
