package com.vik.babiesdayschedule.models;

/**
 * Created by User on 03.01.2017.
 */
public enum SegmentType {
    SLEEP(false), EAT(false), DIAPER(true);
    boolean isShortSegment;

    SegmentType(boolean isShortSegment) {
        this.isShortSegment = isShortSegment;
    }

    public boolean isShortSegment() {
        return isShortSegment;
    }
}
