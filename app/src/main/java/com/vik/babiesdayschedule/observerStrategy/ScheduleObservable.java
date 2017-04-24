package com.vik.babiesdayschedule.observerStrategy;

import java.util.Observer;

/**
 * Created by User on 13.01.2017.
 */
public interface ScheduleObservable {
    void addScheduleObserver(ScheduleObserver o);
    ScheduleObserver removeScheduleObserver(ScheduleObserver o);
    void notifyScheduleObserver();

}
