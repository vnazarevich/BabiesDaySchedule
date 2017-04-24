package com.vik.babiesdayschedule.utils;

import android.util.Log;

import com.vik.babiesdayschedule.R;
import com.vik.babiesdayschedule.Settings;

/**
 * Created by User on 26.02.2017.
 */
public class DurationFormatter {
    static public String getDuration (int minutesSum){
        int hours = minutesSum / 60;
        int minutes = minutesSum % 60;

        switch (hours){
            case 0:
                return analyzeMinutes(minutes);
            case 1:
                return hours +" " + Settings.RESOURCES.getString(R.string.hour) + " " + analyzeMinutes(minutes);
            default:
                return hours +" " + Settings.RESOURCES.getString(R.string.hours) + " " + analyzeMinutes(minutes);
        }
    }

    public static String getDurationAndTimes(int sumInMinutes, int sumTimes) {
                return getDuration(sumInMinutes) +"," + getTimes(sumTimes);
    }

    public static String getTimes(int sumTimes) {
        switch (sumTimes){
            case 1:
                return " " + sumTimes + " " + Settings.RESOURCES.getString(R.string.time) ;
            default:
                return " " + sumTimes + " " + Settings.RESOURCES.getString(R.string.times) ;
        }
    }

    private static String analyzeMinutes(int minutes) {
        switch (minutes){
            case 1 :
                return minutes + " " + Settings.RESOURCES.getString(R.string.minute);
            default:
                return minutes + " " + Settings.RESOURCES.getString(R.string.minutes);
        }
    }
}
