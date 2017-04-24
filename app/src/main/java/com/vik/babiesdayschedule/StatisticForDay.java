package com.vik.babiesdayschedule;

import android.util.Log;

import com.vik.babiesdayschedule.models.Schedule;
import com.vik.babiesdayschedule.models.Segment;
import com.vik.babiesdayschedule.utils.DurationFormatter;

import java.util.List;

/**
 * Created by User on 25.02.2017.
 */
public class StatisticForDay {
    private Schedule schedule;
    private List<Segment> segments;
    private int sleepSumInMinutes;
    private int eatSumInMinutes;
    private int sleepSumTimes;
    private int eatSumTimes;
    private int diaperSum;
    private int peeSum;
    private int pooSum;


    public StatisticForDay() {
        schedule = DataManager.getInstance().getCurrentSchedule();
        makeStatistic();
    }

    public StatisticForDay(Schedule schedule) {
        this.schedule = schedule;
        makeStatistic();

    }

    private void makeStatistic() {
        segments = schedule.getSegments();
        sleepSumInMinutes = 0;
        eatSumInMinutes = 0;
        diaperSum = 0;
        sleepSumTimes = 0;
        eatSumTimes = 0;
        peeSum = pooSum = 0;
        for (Segment s : segments) {
            switch (s.getType().toString()){
                case "SLEEP":
                    sleepSumInMinutes += s.getDurationInMinutes();
                    sleepSumTimes += 1;
                    break;
                case "EAT":
                    eatSumInMinutes += s.getDurationInMinutes();
                    eatSumTimes += 1;
                    break;
                case "DIAPER":
                    diaperSum += 1;
                    if (s.isPee()) {peeSum +=1;}
                    if (s.isPoo()) {pooSum +=1;}
            }
        }
    }

    public String getSleepSum_shortString(){
        return DurationFormatter.getDurationAndTimes(sleepSumInMinutes, sleepSumTimes);
//        if(sleepSumInMinutes < 60){
//            return sleepSumInMinutes+"";
//        } else{
//            return String.format("%d:%02d",sleepSumInMinutes / 60, sleepSumInMinutes % 60);
//        }
    }

    public String getEatSum_shortString(){
        return DurationFormatter.getDurationAndTimes(eatSumInMinutes,eatSumTimes);
//        if(eatSumInMinutes < 60){
//            return eatSumInMinutes+"";
//        } else{
//            return String.format("%d:%02d", eatSumInMinutes / 60, eatSumInMinutes % 60);
//        }
    }

    public String getDiaperSum_shortString(){
        return DurationFormatter.getTimes(diaperSum) + " (" + pooSum + Settings.RESOURCES.getString(R.string.poo) + ")";
    }

    public int getSleepSumInMinutes() {
        return sleepSumInMinutes;
    }

    public int getEatSumInMinutes() {
        return eatSumInMinutes;
    }

    public int getDiaperSum() {
        return diaperSum;
    }
}
