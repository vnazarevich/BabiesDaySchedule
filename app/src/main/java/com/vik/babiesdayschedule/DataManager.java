package com.vik.babiesdayschedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.gson.Gson;
import com.vik.babiesdayschedule.activities.MainActivity;
import com.vik.babiesdayschedule.models.Schedule;
import com.vik.babiesdayschedule.models.Segment;
import com.vik.babiesdayschedule.observerStrategy.ScheduleObservableImp;
import com.vik.babiesdayschedule.utils.TinyDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 27.12.2016.
 */
public class DataManager {
    private final static String OBJECT_KEY = "Segments";
    private static DataManager instance;
    private ArrayList<Schedule> schedules;
    private ScheduleObservableImp observable;
    private Schedule scheduleForDetailsFragment;
    private Schedule currentSchedule;
    private static Settings settings;

    private DataManager() {
        observable = new ScheduleObservableImp();
        initialization();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
            Log.w("+++ DataManager ", "from getInstance() -> schedules = null, call DataManager() ");
        }
        return instance;
    }

    public void initialization() {
        // first initialisation
        if (schedules == null) {
            schedules = new ArrayList<>();
            // schedules.add(new Schedule());
        }
    }

    public Schedule getCurrentSchedule() {

        if (currentSchedule != null) {
            return currentSchedule;
        } else {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(new Date());

            for (Schedule sd : schedules) {
                if (sd.getDate().equals(currentDate)) {
                    scheduleForDetailsFragment = sd;
                    return sd;
                }
            }

            // if Schedule does not exist
            Schedule currentSchedule = new Schedule();
            scheduleForDetailsFragment = currentSchedule;
            schedules.add(currentSchedule);
            return currentSchedule;
        }
    }

    public Schedule getSchedule(Calendar date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date.getTime());

        for (Schedule sd : schedules) {
            if (sd.getDate().equals(currentDate)) {
                scheduleForDetailsFragment = sd;
               // Log.i("+++ DataManager ", " from getSchedule -> schedule " + sdf + " - exist");
                return sd;
            }
        }
        // if schedule with asking date does not exist ->  create new Schedule
        Log.w("+++ DataManager ", " from getSchedule -> schedule " + sdf + " - does not exist, create new Schedule ");
        Schedule currentSchedule = new Schedule(date);
        scheduleForDetailsFragment = currentSchedule;
        schedules.add(currentSchedule);
        return currentSchedule;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public ScheduleObservableImp getObservable() {
        return observable;
    }

    public Schedule getScheduleForDetailsFragment() {
        return scheduleForDetailsFragment;
    }

    public void setScheduleForDetailsFragment(Schedule scheduleForDetailsFragment) {
        this.scheduleForDetailsFragment = scheduleForDetailsFragment;
    }

    public static void saveDataToSharedPreference(Context context) { 
        Log.w("+++ DataManager ", " saved data to shared preference ");
        TinyDB tinyDB = new TinyDB(context);
        // instance.schedules = new ArrayList<>();
        tinyDB.putListObject(Settings.OBJECT_KEY, instance.schedules);
    }

    /**
     * Load ArrayList<Schedule> from SharedPreference
     * and if size of it > 0 and segments != null then recovery link currentSegment = segments.get(size -1)
     */
    public static void loadDataFromPreferences(Context context) {
        Log.w("DataManager +++ ", " load data from Preferences, schedules.size() = " + instance.schedules.size() );
        TinyDB tinyDB = new TinyDB(context);
        instance.schedules = tinyDB.getListObject(Settings.OBJECT_KEY);

        // Deep copy:
        // initialize Set <Schedule> segmentsInProgress
        if (!instance.schedules.isEmpty()) {
            List<Segment> segments;
            for (Schedule schedule : instance.schedules) {
                Log.w("DataManager +++ ", " load data from Preferences: " + schedule.toStringShort());
                segments = schedule.getSegments();

                if (!segments.isEmpty()) {
                    for (Segment segment : segments) {

                        if (null == segment.getFinishTime()) {
                            schedule.getSegmentsInProgress().add(segment);
                        }
                    }
                }
            }
        }
        //for initialisation scheduleForDetailsFragment
        instance.getCurrentSchedule();

        //cleanData();
    }

    private static void cleanData() {
        List<Schedule> deleteList = new ArrayList<>();
        int i=0;

       // instance.schedules.get(instance.schedules.size()-1).getCalendar().set(Calendar.DAY_OF_MONTH,15);


        for(Schedule s: instance.schedules){
            if(s.getCalendar().get(Calendar.DAY_OF_MONTH)==13 && i == 1) {
                s.getCalendar().set(Calendar.DAY_OF_MONTH, 14);
            }
            if(s.getCalendar().get(Calendar.DAY_OF_MONTH)==13 && i == 0){
                Log.e("+++++++++++++++", "+++++++++++++++++++");

                i++;
//            if(s.getSegments().size() == 0){
              //  deleteList.add(s);
            }


        }

        for(Schedule s: deleteList){
            instance.schedules.remove(s);
        }

    }

    //TODO check if segment belong to one day
    public void addNewSegment(Segment segment) {
        //getCurrentSchedule().getSegments().add(segment);
        scheduleForDetailsFragment.getSegments().add(segment);
        Collections.sort(getCurrentSchedule().getSegments());
        Collections.reverse(getCurrentSchedule().getSegments());
        observable.notifyScheduleObserver();
    }

    public void setScheduleForDetailsFragment(Calendar date) {
        getSchedule(date);
    }
}
