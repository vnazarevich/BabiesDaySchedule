package com.vik.babiesdayschedule;

import android.content.res.Resources;

import com.vik.babiesdayschedule.models.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 23.01.2017.
 */
public class Settings {
    public final static String OBJECT_KEY = "Segments1";
    public final static String PREFERENCE_FILE_NAME = "BabiesSchedule1253.xml";
    public  static Resources RESOURCES;


    public static Resources getRESOURCES() {
        return RESOURCES;
    }


    public static void setRESOURCES(Resources RESOURCES) {
        Settings.RESOURCES = RESOURCES;
    }
}
