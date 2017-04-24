package com.vik.babiesdayschedule.models;

import android.util.Log;

import com.vik.babiesdayschedule.DataManager;
import com.vik.babiesdayschedule.StatisticForDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by User on 28.12.2016.
 */
public class Schedule {
    private String date;
    private Calendar calendar;
    private List<Segment> segments;
    private Segment detailsSegment;
    private Set<Segment> segmentsInProgress;
    private StatisticForDay statistic;


    //TODO !!!
    private static final int START_X = 20;

    public Calendar getCalendar() {
        return calendar;
    }

    private static final int START_Y = 50;
    private static final int END_Y = 150;
    private static final int SCHEDULE_LENGTH = 800;

    public Schedule() {
        calendar = GregorianCalendar.getInstance();
        date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        segments = new ArrayList<>();
        segmentsInProgress = new HashSet<>();
    }

    public Schedule(Calendar calendar) {
        this.calendar = calendar;
        date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        segments = new ArrayList<>();
        segmentsInProgress = new HashSet<>();
    }

    public Segment startCreatingSegment(SegmentType type) {
        // checkLastSegment();
        Segment currentSegment = new Segment(type);
        segmentsInProgress.add(currentSegment);
        // currentSegment.setType(type);
//        if (segments.size() == 0) {
//            currentSegment.setStartX(START_X);
//        } else {
//            currentSegment.setStartX(segments.get(segments.size() - 1).getEndX());
//        }
        currentSegment.setStartTime();
        segments.add(0,currentSegment);
        Log.w("+++  Schedule", "from startCreatingSegment() " + currentSegment.toString());
        if (currentSegment.getType().isShortSegment) {
            finishCreatingSegment(type);
        }
        return currentSegment;
    }

    public void finishCreatingSegment(SegmentType type) {
        Segment currentSegment;
        // check if this Segment belong to one Schedule
        if (segmentsInProgress.isEmpty()) {
            //finish segment from previous day
            Schedule previousDaySchedule = DataManager.getInstance().getSchedules().get(
                    DataManager.getInstance().getSchedules().size() - 2);
            Segment currentSegmentFromPreviousDay = previousDaySchedule.getCurrentSegment(type);
            currentSegmentFromPreviousDay.setFinishTime(23, 59);
            previousDaySchedule.getSegmentsInProgress().remove(currentSegmentFromPreviousDay);

            //start new Segment in this day
            currentSegment = new Segment(type);
            currentSegment.setStartTime(0, 0);
            currentSegment.setFinishTime();

        } else {
            currentSegment = getCurrentSegment(type);
            currentSegment.setFinishTime();
            getSegmentsInProgress().remove(currentSegment);
        }


//        if (currentSegment != null) {
//            currentSegment.setFinishTime();
//            double percentage = ((currentSegment.getFinishTime().get(Calendar.HOUR_OF_DAY) - currentSegment.getStartTime().get(Calendar.HOUR_OF_DAY)) * 60
//                    + (currentSegment.getFinishTime().get(Calendar.MINUTE) - currentSegment.getStartTime().get(Calendar.MINUTE))) / (24. * 60);
//            //   (currentSegment.getTimeFinish()-currentSegment.getTimeStart())/(24.*60*60*60);
//            Log.w("+++  Schedule ", "from finishCreatingSegment -> " + currentSegment.getType() + " percentage = " + percentage);
//            currentSegment.setEndX(currentSegment.getStartX() + (int) (percentage * SCHEDULE_LENGTH) + 20);
//            currentSegment.setEndY(END_Y);
//            currentSegment.setStartY(START_Y);
//            currentSegment.createRect();
//            Log.w("+++  Schedule  ", "from finishCreatingSegment -> " + DataManager.getInstance().getCurrentSchedule().getCurrentSegment().toString());
//       } else {
//            Log.w("+++  Schedule  ", "from finishCreatingSegment  ->  currentSegment == null");
//        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "date='" + date + '\'' +
                ", detailsSegment=" + detailsSegment +
                ", segments.size = " + segments.size() +
                ", segments= " + segments.toString() +
                '}';
    }
    public String toStringShort() {
        return "Schedule{" +
                "date='" + date + '\'' +
//                ", detailsSegment=" + detailsSegment +
                ", segments.size = " + segments.size()+
                '}';
    }

    public void clearSegments() {
        segments.clear();
    }

    public Segment getDetailsSegment() {
        return detailsSegment;
    }

    public void setDetailsSegment(Segment detailsSegment) {
        this.detailsSegment = detailsSegment;
    }

    public Segment getCurrentSegment(SegmentType type) {
        try {
            for (Segment s : segmentsInProgress) {
                if (s.getType().equals(type)) {
                    return s;
                }
            }
        } catch (NullPointerException ex) {
            Log.w("+++ Schedule", " from getCurrentSegment -> NullPointerException");
            ex.printStackTrace();
        }
        return null;
    }

    public Set<Segment> getSegmentsInProgress() {
        return segmentsInProgress;
    }

    public void setSegmentsInProgress(Set<Segment> segmentsInProgress) {
        this.segmentsInProgress = segmentsInProgress;
    }

    // TODO зробити нормальну статистику, а не плодити об'єкти
    public StatisticForDay getStatistic() {
        return new StatisticForDay(this);
        //return statistic;
    }
}
