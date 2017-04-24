package com.vik.babiesdayschedule.observerStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 13.01.2017.
 */
public class ScheduleObservableImp implements ScheduleObservable{
    private List<ScheduleObserver> observers;

    public ScheduleObservableImp() {
        observers = new ArrayList();
    }

    @Override
    public void addScheduleObserver(ScheduleObserver observer) {
        observers.add(observer);

    }

    @Override
    public ScheduleObserver removeScheduleObserver(ScheduleObserver observer) {
        observers.remove(observer);
        return observer;
    }

    @Override
    public void notifyScheduleObserver() {
        for(ScheduleObserver observer: observers) {
            observer.refresh();
        }
    }
}

