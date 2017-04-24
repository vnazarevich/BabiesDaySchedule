package com.vik.babiesdayschedule.fragments;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.vik.babiesdayschedule.DataManager;
import com.vik.babiesdayschedule.R;
import com.vik.babiesdayschedule.Settings;
import com.vik.babiesdayschedule.activities.MainActivity;
import com.vik.babiesdayschedule.models.Schedule;
import com.vik.babiesdayschedule.models.Segment;
import com.vik.babiesdayschedule.observerStrategy.ScheduleObserver;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.vik.babiesdayschedule.fragments.SegmentEditFragment.OnSegmentEditFragmentListener} interface
 * to handle interaction events.
 * Use the {@link SegmentEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SegmentEditFragment extends Fragment implements ScheduleObserver {
    private final static String TAG1 = "DayListFragment";
    private SegmentEditManager mSegmentEditManager;
    private int hourFrom, minuteFrom, hourTo, minuteTo;
    private View    view;
    private Segment segment;
    private Schedule schedule;
    private TextView title;
    private TextView from;
    private TextView to;
    private EditText timeFrom;
    private EditText timeTo;
    private TextView duration;
    private Button btn_editFrom;
    private Button btn_editTo;
    private Button btn_save;
    private long startTimeForDuration;
    private boolean isStartTimeChanged;
    private boolean isFinishTimeChanged;
    private boolean stopTimer;

    private OnSegmentEditFragmentListener mListener;
    Handler timerHandler = new Handler();
    Runnable timerRunnable;

    private void startTimer() {

        //runs without a timer by reposting this handler at the end of the runnable
        timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTimeForDuration;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                duration.setText(String.format("%d:%02d", minutes, seconds));

                if ( !stopTimer) {
                    timerHandler.postDelayed(this, 500);
                }
            }
        };

        timerHandler.postDelayed(timerRunnable, 0);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SegmentEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SegmentEditFragment newInstance(String param1, String param2) {
        SegmentEditFragment fragment = new SegmentEditFragment();
        Bundle args = new Bundle();
        args.putString( "ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SegmentEditFragment() {

    }

    //TODO +++ Use Bundle and method         if (getArguments() != null) {
   // mParam1 = getArguments().getString(ARG_PARAM1);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO schedule - має бути не один (ScheduleForDetailsFragment) , а повинен задаватись датою, яку вибере юзер
       try{
        schedule = DataManager.getInstance().getScheduleForDetailsFragment();
        segment = schedule.getDetailsSegment();
        mSegmentEditManager = new SegmentEditManager();
        } catch (NullPointerException e){
            Log.e("SegmentEditFragment +++ from onCreate() ->", "NullPointerException" );
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_segment_edit, container, false);
        initializeComponents();
        fillComponents();
//        timerHandler.postDelayed(timerRunnable, 0);

        //startTimer();
        return view;
    }

    private void initializeComponents() {
        isStartTimeChanged = false;
        isFinishTimeChanged = false;
        stopTimer = false;

        title = (TextView) view.findViewById(R.id.title);
        from = (TextView) view.findViewById(R.id.from);
        timeFrom = (EditText) view.findViewById(R.id.from_time);
        timeTo = (EditText) view.findViewById(R.id.to_time);
        to = (TextView) view.findViewById(R.id.to);
        duration = (TextView) view.findViewById(R.id.duration);
        btn_editFrom = (Button) view.findViewById(R.id.btn_edit_from);
        btn_editTo = (Button) view.findViewById(R.id.btn_edit_to);
        btn_save = (Button) view.findViewById(R.id.btn_save);

        btn_editFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeFrom.setText(String.format("%02d:%02d", hourOfDay, minute));
                        mSegmentEditManager.saveNewTimeStartData(hourOfDay, minute);
                        isStartTimeChanged = true;
                        if (segment.getFinishTime() == null){
                            refreshTimer(hourOfDay, minute);
                        } else{
                            duration.setText(mSegmentEditManager.getDuration());
                        }
                        hourTo = hourOfDay;
                        minuteTo = minute;
                    }

                    private void refreshTimer(int hourOfDay, int minute) {
                        Calendar temp = Calendar.getInstance();
                        temp.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        temp.set(Calendar.MINUTE, minute);
                        stopTimer = true;
                        startTimeForDuration = temp.getTimeInMillis();
                        stopTimer = false;
                        startTimer();
                    }
                }, hourFrom, minuteFrom, DateFormat.is24HourFormat(getActivity())  );
                timePickerDialog.show();
            }
        });

        btn_editTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                       timerHandler.removeCallbacks(timerRunnable);
                       timeTo.setText(String.format("%02d:%02d", hourOfDay, minute));
                        mSegmentEditManager.saveNewTimeFinishData(hourOfDay, minute);
                        isFinishTimeChanged = true;
                        stopTimer = true;
                        duration.setText(mSegmentEditManager.getDuration());
                        hourTo = hourOfDay;
                        minuteTo = minute;
                    }
                }, hourTo, minuteTo, DateFormat.is24HourFormat(getActivity())  );
                timePickerDialog.show();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSegmentEditManager.onSave();
            }
        });

    }

    private void fillComponents() {

        if (segment != null) {
            title.setText(segment.getType().toString());
            from.setText("FROM:  ");
            to.setText("TO:    ");
            hourFrom = segment.getStartTime().get(Calendar.HOUR_OF_DAY);
            minuteFrom = segment.getStartTime().get(Calendar.MINUTE);
            timeFrom.setText(String.format("%02d:%02d", hourFrom, minuteFrom));
            startTimeForDuration = segment.getStartTime().getTimeInMillis();

            if (segment.getFinishTime() != null){
                hourTo =  segment.getFinishTime().get(Calendar.HOUR_OF_DAY);
                minuteTo = segment.getFinishTime().get(Calendar.MINUTE);
                timeTo.setText(String.format("%02d:%02d", hourTo, minuteTo));
                //TODO Duration можна зробити в Segment
                //TODO якщо години = 0
                duration.setText(mSegmentEditManager.getDuration());
//                duration.setText("DURATION : " + (segment.getFinishTime().get(Calendar.HOUR_OF_DAY) - segment.getStartTime().get(Calendar.HOUR_OF_DAY)) + " : "
//                                + (segment.getFinishTime().get(Calendar.MINUTE)- segment.getStartTime().get(Calendar.MINUTE)) );
            } else {
                startTimer();
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
//TODO read about OnSegmentEditFragmentListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSegmentEditFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void refresh() {
        segment = DataManager.getInstance().getScheduleForDetailsFragment().getDetailsSegment();
        fillComponents();
    }

    @Override
    public void registerAsObserver() {
        DataManager.getInstance().getObservable().addScheduleObserver(this);
    }

    class SegmentEditManager {
        private final static String TAG3 = "CleanFragments";
        private int startHour;
        private int startMinute;
        private int finishHour;
        private int finishMinute;
        Calendar startTime;
        Calendar finishTime;

        SegmentEditManager(){
            startHour = segment.getStartTime().get(Calendar.HOUR_OF_DAY);
            startMinute = segment.getStartTime().get(Calendar.MINUTE);
            if (segment.getFinishTime() != null){
                finishTime = segment.getFinishTime();
                finishHour = finishTime.get(Calendar.HOUR_OF_DAY);
                finishMinute = finishTime.get(Calendar.MINUTE);
            }
        }

        //TODO ask user input valid data
        void onSave(){
            if(finishTime == null){
                Log.i("+++ SegmentEditFragment", "from EditFragmentManager.onSave() -> user changed startTime, action is still runing");
                changeSegmentsStartTime();
                startTimeForDuration = segment.getStartTime().getTimeInMillis();

                //segment.refreshSegment();
                DataManager.getInstance().getObservable().notifyScheduleObserver();
                ((MainActivity)getActivity()).initializeFragments(TAG1);
                return;
            }

            if (isDataValid()){
                Log.i("+++ SegmentEditFragment", "from EditFragmentManager.onSave() -> user input valid data, isFinishTimeChanged=" + isFinishTimeChanged);
                changeSegmentsStartTime();
                changeSegmentsFinishTime();
                schedule.getSegmentsInProgress().remove(segment);
                segment.refreshSegment();
                ((MainActivity)getActivity()).initializeFragments(TAG1);
               // onDestroy();
            } else {
                Log.e("+++ SegmentEditFragment", "from EditFragmentManager.onSave() -> user input invalid data in editFragment");
            }
        }

        private void changeSegmentsFinishTime() {
            if (isFinishTimeChanged){
                segment.setFinishTime(finishHour, finishMinute);
            }
            isFinishTimeChanged = false;
        }

        private void changeSegmentsStartTime() {
            if (isStartTimeChanged){
                segment.setStartTime(startHour, startMinute);
            }
            isStartTimeChanged = false;
        }

        private void saveNewTimeStartData(int hourOfDay, int minute) {
            startHour = hourOfDay;
            startMinute = minute;

        }

        private void saveNewTimeFinishData(int hourOfDay, int minute) {
            finishHour = hourOfDay;
            finishMinute = minute;
            finishTime = Calendar.getInstance();
            finishTime.set(Calendar.HOUR_OF_DAY, finishHour);
            finishTime.set(Calendar.MINUTE, finishMinute);
        }

        private String getDuration() {
//            if ((finishHour - startHour) > 0 ){
//                return String.format("%d:%02d", (finishHour - startHour), (finishMinute - startMinute));
//            } else {
//                return //String.format("%02d",
//                        (finishMinute - startMinute)
//                        + " " + getActivity().getResources().getString(R.string.shortMinute);
//            }

            if(finishHour == startHour){
                return (finishMinute - startMinute) + " "
                       + getActivity().getResources().getString(R.string.shortMinute);

            }else if(finishMinute < startMinute){
                return String.format("%d:%02d", (finishHour - startHour - 1), (60 + finishMinute - startMinute));

            }else {
                return String.format("%d:%02d", (finishHour - startHour), (finishMinute - startMinute));
            }
        }

        //TODO validate not only time, but date too
        private boolean isDataValid (){
            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, startHour);
            startTime.set(Calendar.MINUTE, startMinute);

            return (finishTime.getTimeInMillis() - startTime.getTimeInMillis()) > 0 ;
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSegmentEditFragmentListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
