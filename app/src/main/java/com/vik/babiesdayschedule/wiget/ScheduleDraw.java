package com.vik.babiesdayschedule.wiget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.vik.babiesdayschedule.DataManager;
import com.vik.babiesdayschedule.R;
import com.vik.babiesdayschedule.Settings;
import com.vik.babiesdayschedule.UsersSettings;
import com.vik.babiesdayschedule.models.SegmentType;
import com.vik.babiesdayschedule.models.Schedule;
import com.vik.babiesdayschedule.models.Segment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by User on 25.12.2016.
 */
public class ScheduleDraw extends View {
    private final static int COLUMNS_VALUE = 14;
    private final static int LINES_VALUE = 24;

    private List <Schedule> schedules;
    private  List <Segment> segments;
    private Calendar calendar;

    private float graphicWidth;
    private float graphicHeight;
    //grill coordinates:
    private float startX, startY, stopX, stopY;
    private float stepForX, stepForY;
    private int durationMinimum;


    //TODO do we need this var?
    private String currentDate;
    private Paint paint;
    private Paint paintText;
    private float minimumSegmentHeight = 5f;

    public ScheduleDraw(Context context) {
        super(context);
    }
    public ScheduleDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ScheduleDraw(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {

        graphicWidth = getWidth() - getPaddingLeft() - getPaddingRight() - Settings.RESOURCES.getDimension(R.dimen.paddingForTextLeftSide);
        graphicHeight = getHeight() - getPaddingTop() - getPaddingBottom() - Settings.RESOURCES.getDimension(R.dimen.paddingForTextBottomSide);

        paint = new Paint();

        paintText = new Paint();
        paintText.setColor(Settings.RESOURCES.getColor(R.color.text_color));
        paintText.setStrokeWidth(Settings.RESOURCES.getDimension(R.dimen.graphicTextWidth));
        paintText.isLinearText();
        paintText.setStyle(Paint.Style.FILL);

        calendar = GregorianCalendar.getInstance();
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        schedules = DataManager.getInstance().getSchedules();
       // durationMinimum = graphicHeight/24/60*
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("+++ ScheduleDraw ",  " start draw ");
        init();
        paintGrille(canvas);
        paintGraphic(canvas);
      //  Log.w("+++ onDraw View info",  this.getWidth()+", " + this.getHeight() +", " + this.getTop());
    }

    private void paintGraphic(Canvas canvas) {
        Schedule schedule;
        float columnWeight = (stopX - startX) / COLUMNS_VALUE;
        float scheduleAxisX = stopX - (columnWeight / 2);
        paintText.setTextSize(Settings.RESOURCES.getDimension(R.dimen.graphic_text_size));

        for(int i=0; i<COLUMNS_VALUE; i++){
            schedule = schedules.get(schedules.size()-1-i);
            drawSleepSegments(scheduleAxisX, schedule.getSegments(), canvas);
            drawOtherSegments(scheduleAxisX, schedule.getSegments(), canvas);
            drawTextDate(scheduleAxisX-Settings.RESOURCES.getDimension(R.dimen.graphic_text_margin_left), schedule.getCalendar(), canvas);
            scheduleAxisX -= columnWeight;
        }
    }

    private void drawTextDate(float x, Calendar date, Canvas canvas) {
      canvas.drawText(String.format("%td-%tb", date, date), x, (stopY-Settings.RESOURCES.getDimension(R.dimen.graphic_text_margin_bottom) + Settings.RESOURCES.getDimension(R.dimen.paddingForTextBottomSide)), paintText);
    }

    private void drawOtherSegments(float scheduleAxisX, List<Segment> segments, Canvas canvas) {
        for (Segment s: segments){
            if( ! s.getType().toString().equals("SLEEP")) {
                drawSegment(scheduleAxisX, s, canvas);
            }
        }
    }

    private void drawSleepSegments(float scheduleAxisX, List<Segment> segments, Canvas canvas) {
        for (Segment s: segments){
            if (s.getType().toString().equals("SLEEP")){
                drawSegment(scheduleAxisX, s, canvas);
            }
        }
    }

    private void drawSegment(float scheduleAxisX, Segment s, Canvas canvas) {
        paint.setStrokeWidth((float)60.);
        paint.setColor(s.getColor());
        paint.setStyle(Paint.Style.STROKE);

        if(s.getType().isShortSegment()){
            canvas.drawLine(scheduleAxisX, getY(s.getStartTime()), scheduleAxisX, getY(s.getStartTime())+ minimumSegmentHeight, paint);
            return;
        }
        if (s.getFinishTime() != null) {
           //Log.i("+++ onDraw ", s.getType() +", "+ s.getColor()+ ", getY(s.getStartTime())=" + getY(s.getStartTime()) + ", getY(s.getFinishTime()) = " + getY(s.getFinishTime()));
            canvas.drawLine(scheduleAxisX, getY(s.getStartTime()), scheduleAxisX, getY(s.getFinishTime()), paint);
        }
    }

    private float getY(Calendar time) {
        float minutesOfDay = time.get(Calendar.HOUR_OF_DAY)*60 + time.get(Calendar.MINUTE);
        return minutesOfDay/1440*graphicHeight + getPaddingTop();
    }

    private void paintGrille(Canvas canvas) {
        paint.setStrokeWidth(Settings.RESOURCES.getDimension(R.dimen.grilleWidth));
        paint.setColor(Settings.RESOURCES.getColor(R.color.grilleColor));
        paint.setStyle(Paint.Style.STROKE);

        startX = getPaddingLeft() + Settings.RESOURCES.getDimension(R.dimen.paddingForTextLeftSide);
        startY = getPaddingTop();
        stopY = getPaddingTop() + getHeight()- Settings.RESOURCES.getDimension(R.dimen.paddingForTextBottomSide) ;
        stopX = getWidth() - getPaddingRight();
        //float startY = getHeight() - getPaddingBottom() - Settings.RESOURCES.getDimension(R.dimen.paddingForTextBottomSide);
        //float stopY = getPaddingBottom() ;
        stepForX = (getWidth() - getPaddingLeft() - getPaddingRight() - Settings.RESOURCES.getDimension(R.dimen.paddingForTextLeftSide))/COLUMNS_VALUE;
        stepForY = (getHeight() - getPaddingTop() - getPaddingBottom() - Settings.RESOURCES.getDimension(R.dimen.paddingForTextBottomSide))/LINES_VALUE;

        float x = startX;
        for (int i = 0; i <= COLUMNS_VALUE; i++){
             canvas.drawLine(x, startY, x, stopY, paint);
             x += stepForX;
        }

        float y = startY;
        float yForText = y + Settings.RESOURCES.getDimension(R.dimen.graphic_text_size)/2;
        float xForText = startX - Settings.RESOURCES.getDimension(R.dimen.paddingForTextLeftSide);
        paintText.setTextSize(Settings.RESOURCES.getDimension(R.dimen.graphic_text_size_hours));

        for (int i = 0;  i <= LINES_VALUE; i++){
            if (i == UsersSettings.getStartDayHour() || i == UsersSettings.getFinishDayHour()){

                paint.setColor(Settings.RESOURCES.getColor(R.color.text_color));
                paintText.setStyle(Paint.Style.STROKE);

                canvas.drawLine(startX, y, stopX, y, paint);
                canvas.drawText("" + i, xForText, yForText, paintText);

                paint.setColor(Settings.RESOURCES.getColor(R.color.grilleColor));
                paintText.setStyle(Paint.Style.FILL);
            }
           else {
                canvas.drawLine(startX, y, stopX, y, paint);
                canvas.drawText(""+i, xForText, yForText, paintText);
            }
            yForText += stepForY;
            y += stepForY;
        }

    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    //TODO колір має бути пропертею SegmentType
    private int getSegmentColor(SegmentType type) {
        int color=0;
        switch (type){
            case SLEEP:
                color = getResources().getColor(R.color.purple);
                break;
            case EAT:
                color = getResources().getColor(R.color.light_blue);
                break;
            case DIAPER:
                color = getResources().getColor(R.color.light_green);
                break;
        }
        return color;
    }
}
