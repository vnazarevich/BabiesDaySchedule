package com.vik.babiesdayschedule;

import java.util.Calendar;

/**
 * Created by User on 15.03.2017.
 */
public class UsersSettings {
    private static int startDayHour;
    private static int finishDayHour;
    public static Calendar dateSelectedByUser;

    public static int getStartDayHour() {
        if (startDayHour == 0){
            startDayHour = 8;
        }
        return startDayHour;
    }

    public static void setStartDayHour(int startDayHour) {
        UsersSettings.startDayHour = startDayHour;
    }

    public static int getFinishDayHour() {
        if (finishDayHour == 0){
            finishDayHour = 22;
        }

        return finishDayHour;
    }

    public static void setFinishDayHour(int finishDayHour) {
        UsersSettings.finishDayHour = finishDayHour;
    }

    public static Calendar getDateSelectedByUser() {
        if (dateSelectedByUser == null){
            dateSelectedByUser = Calendar.getInstance();
        }
        return dateSelectedByUser;
    }

    public static void setDateSelectedByUser(Calendar dateSelectedByUser) {
        UsersSettings.dateSelectedByUser = dateSelectedByUser;
    }
}
