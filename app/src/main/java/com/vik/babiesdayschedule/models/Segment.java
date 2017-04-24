package com.vik.babiesdayschedule.models;

import android.graphics.Rect;
import android.util.Log;

import com.vik.babiesdayschedule.DataManager;
import com.vik.babiesdayschedule.R;
import com.vik.babiesdayschedule.Settings;
import com.vik.babiesdayschedule.utils.DurationFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by User on 25.12.2016.
 */
public class Segment implements Comparable<Segment> {
    private int startX, startY, endX, endY;
    private int color;
    private Rect rect;
    private SegmentType type;
    private Calendar startTime;
    private Calendar finishTime;
    private String duration;
    private int durationInMinutes;
    private boolean isPoo, isPee;

    public Segment() {

    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime() {
        this.startTime = new GregorianCalendar();
    }

    public Calendar getFinishTime() {
        return finishTime;
    }

    public void setFinishTime() {
        this.finishTime = Calendar.getInstance();
        countDuration();
    }

    public SegmentType getType() {
        return type;
    }

    public void setType(SegmentType type) {
        this.type = type;
        setSegmentColor(type);
    }

    private void setSegmentColor(SegmentType type) {
        switch (type){
            case SLEEP:
                color = Settings.RESOURCES.getColor(R.color.purple);
                break;
            case EAT:
                color = Settings.RESOURCES.getColor(R.color.light_blue);
                break;
            case DIAPER:
                color = Settings.RESOURCES.getColor(R.color.brown);
                break;
        }
    }

    public Segment(SegmentType type) {
        this.type = type;
        rect = new Rect();
        // generateColor(type);

    }

    public Segment(int color, int startX, int startY, int endX, int endY) {
        this.color = color;
        this.endX = endX;
        this.endY = endY;
        this.startX = startX;
        this.startY = startY;
        // rect = new Rect(startX, startY, endX, endY );
    }

//    public Segment(SegmentType type, int startX, int startY, int endX, int endY) {
//        this(type);
//
//        this.endX = endX;
//        this.endY = endY;
//        this.startX = startX;
//        this.startY = startY;
//        // rect = new Rect(startX, startY, endX, endY );
//    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
        rect = new Rect(startX, startY, endX, endY);
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getColor() {
        if (color == 0){
            setSegmentColor(type);
        }
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Rect getRect() {
        if (rect == null) {
            rect = new Rect(startX, startY, endX, endY);
        }
        return rect;
    }

    public String toStringFull() {
        return "Segment{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                ", color=" + color +
                ", rect=" + rect +
                ", type=" + type +
                ", duration=" + duration +
                '}';
    }

    @Override
    public String toString() {
        try {
            if (type.isShortSegment) {
                StringBuilder sb = new StringBuilder();
                switch (type.toString()){
                    case "DIAPER":
                        sb.append(" (");
                        if (isPee && isPoo){
                            sb.append(Settings.getRESOURCES().getString(R.string.pee) + ", " + Settings.getRESOURCES().getString(R.string.pee));
                        }
                        if (isPee){
                            sb.append(Settings.getRESOURCES().getString(R.string.pee));
                        }
                        if (isPoo){
                            sb.append(Settings.getRESOURCES().getString(R.string.pee));
                        }
                        sb.append(") ");
                        return "" + type + ": "
                                + (String.format("%02d:%02d", startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE))) + sb.toString();

                }
                return "" + type + ": "
                        + (String.format("%02d:%02d", startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE)));


            }
            if (finishTime == null) {
                return "" + type + ": "
                        + (String.format("%02d:%02d", startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE)));
            } else {
                return "" + type + ": "
                        + (String.format("%02d:%02d", startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE))) + " - " + (String.format("%02d:%02d", finishTime.get(Calendar.HOUR_OF_DAY), finishTime.get(Calendar.MINUTE)))
                        + "   ||   " + DurationFormatter.getDuration(durationInMinutes);
            }
        } catch (NullPointerException ex) {
            Log.e("+++ Segment.class", "from toString() -> NullPointerException");
            return null;
        }
    }

    public void createRect() {
        rect = new Rect(startX, startY, endX, endY);
    }

    //TODO drawView, замінити scaleLength

    /**
     * Recount value of X, Y, rect based on new time data
     */
    public void refreshSegment() {
        int scaleLength = 1000;
        startX = (startTime.get(Calendar.HOUR_OF_DAY) * 60 + startTime.get(Calendar.MINUTE)) / 1440 * scaleLength;
        endX = (finishTime.get(Calendar.HOUR_OF_DAY) * 60 + finishTime.get(Calendar.MINUTE)) / 1440 * scaleLength;
        rect = new Rect(startX, startY, endX, endY);
        duration = getDuration();
        DataManager.getInstance().getObservable().notifyScheduleObserver();
        Log.i("+++ Segment", "from refreshSegment() -> Segment" + type + " was refreshed and send notification");
    }

    public String getDuration() {
        int finishHour = finishTime.get(Calendar.HOUR_OF_DAY);
        int finishMinute = finishTime.get(Calendar.MINUTE);
        int startHour = startTime.get(Calendar.HOUR_OF_DAY);
        int startMinute = startTime.get(Calendar.MINUTE);

        if (finishHour == startHour) {
            durationInMinutes = finishMinute - startMinute;
            return (finishMinute - startMinute) + " "
                    + Settings.RESOURCES.getString(R.string.minutes_short);

        } else if (finishMinute < startMinute) {
            durationInMinutes = (finishHour - startHour) * 60 + (finishMinute - startMinute);
            return String.format("%d:%02d", (finishHour - startHour - 1), (60 + finishMinute - startMinute));

        } else {
            durationInMinutes = (finishHour - startHour) * 60 + (finishMinute - startMinute);
            return String.format("%d:%02d", (finishHour - startHour), (finishMinute - startMinute));
        }
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setStartTime(int startHour, int startMinute) {
        if (startTime == null) {
            startTime = Calendar.getInstance();
        }
        startTime.set(Calendar.HOUR_OF_DAY, startHour);
        startTime.set(Calendar.MINUTE, startMinute);
        // refreshSegment();
    }

    public boolean isPoo() {
        return isPoo;
    }

    public boolean isPee() {
        return isPee;
    }

    public void setFinishTime(int finishHour, int finishMinute) {
        if (finishTime == null) {
            finishTime = Calendar.getInstance();
        }

        finishTime.set(Calendar.HOUR_OF_DAY, finishHour);
        finishTime.set(Calendar.MINUTE, finishMinute);
        countDuration();
    }

    private void countDuration() {

            int finishHour = finishTime.get(Calendar.HOUR_OF_DAY);
            int finishMinute = finishTime.get(Calendar.MINUTE);
            int startHour = startTime.get(Calendar.HOUR_OF_DAY);
            int startMinute = startTime.get(Calendar.MINUTE);

            if (finishHour == startHour) {
                durationInMinutes = finishMinute - startMinute;

            } else if (finishMinute < startMinute) {
                durationInMinutes = (finishHour - startHour) * 60 + (finishMinute - startMinute);

            } else {
                durationInMinutes = (finishHour - startHour) * 60 + (finishMinute - startMinute);
            }
    }

    @Override
    public int compareTo(Segment another) {
        return (int) (this.startTime.getTimeInMillis() - another.startTime.getTimeInMillis());
    }

    public void setPooPee(boolean poo, boolean pee) {
        this.isPoo = poo;
        this.isPee = pee;
    }
}
