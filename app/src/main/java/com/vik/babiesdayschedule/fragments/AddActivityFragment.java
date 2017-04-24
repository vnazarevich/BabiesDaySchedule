package com.vik.babiesdayschedule.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.vik.babiesdayschedule.DataManager;
import com.vik.babiesdayschedule.R;
import com.vik.babiesdayschedule.Settings;
import com.vik.babiesdayschedule.activities.MainActivity;
import com.vik.babiesdayschedule.models.Segment;
import com.vik.babiesdayschedule.models.SegmentType;
import com.vik.babiesdayschedule.observerStrategy.ScheduleObserver;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.vik.babiesdayschedule.fragments.SegmentEditFragment.OnSegmentEditFragmentListener} interface
 * to handle interaction events.
 * Use the {@link SegmentEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddActivityFragment extends Fragment implements ScheduleObserver {
    private final static String TAG1 = "DayListFragment";
    private final static String please_input_correct_data = "Please input correct data ";

    private SegmentEditManager mSegmentEditManager;
    private int hourFrom, minuteFrom, hourTo, minuteTo;
    private View    view;
    private Segment segment;
    private TextView title;
    private TextView from;
    private TextView to;
    private EditText timeFrom;
    private EditText timeTo;
    private TextView duration;
    private Button btn_editFrom;
    private Button btn_editTo;
    private Button btn_save;
    private Button btn_cancel;
    long startTimeForDuration;
    boolean isStartTimeChanged;
    boolean isFinishTimeChanged;

    private OnAddActivityFragmentInteractionListener  mListener;

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


    //TODO +++ Use Bundle and method         if (getArguments() != null) {
    // mParam1 = getArguments().getString(ARG_PARAM1);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        segment = new Segment();
        mSegmentEditManager = new SegmentEditManager();
    }

    private void initializeComponents() {
        title = (TextView) view.findViewById(R.id.title);
        from = (TextView) view.findViewById(R.id.from);
        timeFrom = (EditText) view.findViewById(R.id.from_time);
        timeTo = (EditText) view.findViewById(R.id.to_time);
        to = (TextView) view.findViewById(R.id.to);
        duration = (TextView) view.findViewById(R.id.duration_2);
        btn_editFrom = (Button) view.findViewById(R.id.btn_edit_from_2);
        btn_editTo = (Button) view.findViewById(R.id.btn_edit_to_2);
        btn_save = (Button) view.findViewById(R.id.btn_save);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).initializeFragments(TAG1);
            }
        });

        btn_editFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeFrom.setText(String.format("%02d:%02d", hourOfDay, minute));
                        mSegmentEditManager.setDuration(); //startTimeForDuration = segment.getStartTime().getTimeInMillis();//System.currentTimeMillis();
                        mSegmentEditManager.saveNewTimeStartData(hourOfDay, minute);
                        isStartTimeChanged = true;
                        hourTo = hourOfDay;
                        minuteTo = minute;
                    }
                }, hourFrom, minuteFrom, DateFormat.is24HourFormat(getActivity())  );
                timePickerDialog.show();
            }
        });

        btn_editTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //timerHandler.removeCallbacks(timerRunnable);
                        timeTo.setText(String.format("%02d:%02d", hourOfDay, minute));
                        mSegmentEditManager.setDuration(); //duration.setText(mSegmentEditManager.getDuration());
                        mSegmentEditManager.saveNewTimeFinishData(hourOfDay, minute);
                        isFinishTimeChanged = true;
                        hourTo = hourOfDay;
                        minuteTo = minute;
                    }
                }, hourTo, minuteTo, DateFormat.is24HourFormat(getActivity()) );
                // timePickerDialog.setIs24HourView(DateFormat.is24HourFormat(this));
                timePickerDialog.show();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSegmentEditManager.onSave();
            }
        });

        final String[] data = getArrayOfSegmentType();
        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, data );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // выделяем элемент
        final int defaultPosition = 0;
        spinner.setSelection(defaultPosition);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                segment.setType(SegmentType.valueOf(data[position]));
                // показываем позиция нажатого элемента
                //Toast.makeText(getActivity().getApplicationContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                segment.setType(SegmentType.valueOf(data[defaultPosition]));
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_activity, container, false);
        initializeComponents();
        //fillComponents();
        //timerHandler.postDelayed(timerRunnable, 0);
        return view;
    }

    private void fillComponents() {

        if (segment != null) {
            title.setText(segment.getType().toString());
            from.setText("FROM:  ");
            to.setText("TO:    ");
            hourFrom = segment.getStartTime().get(Calendar.HOUR_OF_DAY);
            minuteFrom = segment.getStartTime().get(Calendar.MINUTE);
            timeFrom.setText(hourFrom  + " : " + minuteFrom );
            startTimeForDuration = segment.getStartTime().getTimeInMillis();

            if (segment.getFinishTime() != null){
                hourTo =  segment.getFinishTime().get(Calendar.HOUR_OF_DAY);
                minuteTo = segment.getFinishTime().get(Calendar.MINUTE);
                timeTo.setText(hourTo + " : " + minuteTo );
                //TODO Duration можна зробити в Segment
                //TODO якщо години = 0
//                duration.setText("DURATION : " + (segment.getFinishTime().get(Calendar.HOUR_OF_DAY) - segment.getStartTime().get(Calendar.HOUR_OF_DAY)) + " : "
//                                + (segment.getFinishTime().get(Calendar.MINUTE)- segment.getStartTime().get(Calendar.MINUTE)) );
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
            mListener = (OnAddActivityFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        //timerHandler.removeCallbacks(timerRunnable);
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

    private String[] getArrayOfSegmentType() {
        String [] arr = new String [SegmentType.values().length];
        for(int i = 0; i < SegmentType.values().length; i++){
            arr[i] = SegmentType.values()[i].toString();
        }
        return arr;
    }

    public interface OnAddActivityFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class SegmentEditManager {
        private final static String TAG3 = "CleanFragments";
        private int startHour;
        private int startMinute = -1;
        private int finishHour;
        private int finishMinute = -1;
        Calendar startTime;
        Calendar finishTime;

        SegmentEditManager(){
           // startHour = segment.getStartTime().get(Calendar.HOUR_OF_DAY);
           // startMinute = segment.getStartTime().get(Calendar.MINUTE);
        }

        //TODO ask user input valid data
        void onSave(){
            if (isDataValid()){
                Log.i("+++ SegmentEditFragment", "from EditFragmentManager.onSave() -> user input valid data, isFinishTimeChanged=" + isFinishTimeChanged);
                changeSegmentsStartTime();
                changeSegmentsFinishTime();
                isFinishTimeChanged = false;
                isStartTimeChanged = false;
                segment.refreshSegment();
                DataManager.getInstance().addNewSegment(segment);
                ((MainActivity)getActivity()).initializeFragments(TAG1);
            } else {
                Log.e("+++ AddActivityFragment", "from EditFragmentManager.onSave() -> user input invalid data in editFragment");
                Toast.makeText(getActivity().getApplicationContext(), please_input_correct_data , Toast.LENGTH_SHORT).show();
            }
        }

        private void changeSegmentsFinishTime() {
            if (isFinishTimeChanged){
                segment.setFinishTime(finishHour, finishMinute);
            }
        }

        private void changeSegmentsStartTime() {
            if (isStartTimeChanged){
                segment.setStartTime(startHour, startMinute);
            }
        }

        private void saveNewTimeStartData(int hourOfDay, int minute) {
            startHour = hourOfDay;
            startMinute = minute;
        }

        private void saveNewTimeFinishData(int hourOfDay, int minute) {
            finishHour = hourOfDay;
            finishMinute = minute;
        }

        private String getDuration() {
            if(finishHour == startHour){
                return String.format("%02d",(finishMinute - startMinute)) + " " +
                        Settings.RESOURCES.getString(R.string.minutes_short);

            }else if(finishMinute < startMinute){
                return String.format("%d:%02d", (finishHour - startHour - 1), (60 + finishMinute - startMinute));

            }else {
                return String.format("%d:%02d", (finishHour - startHour), (finishMinute - startMinute));
            }
//
//            if ((finishHour - startHour) > 0 ){
//                return String.format("%d:%02d", (finishHour - startHour), (finishMinute - startMinute));
//            } else {
//                return //String.format("%02d",
//                        (finishMinute - startMinute)
//                                + " " + getActivity().getResources().getString(R.string.shortMinute);
//            }
        }

        //TODO validate not only time, but date too
        private boolean isDataValid (){
            if (startMinute == -1 || finishMinute == -1){
                return false;
            }
            startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, startHour);
            startTime.set(Calendar.MINUTE, startMinute);

            finishTime = Calendar.getInstance();
            finishTime.set(Calendar.HOUR_OF_DAY, finishHour);
            finishTime.set(Calendar.MINUTE, finishMinute);

            return (finishTime.getTimeInMillis() - startTime.getTimeInMillis()) > 0 ;
        }

        public void setDuration() {
            if(isDataValid()){
                //duration.setText(segment.getDuration());
                duration.setText(getDuration());
            }
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
//    public interface OnSegmentEditFragmentListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}
//
//
//package com.vik.babiesdayschedule.fragments;
//
//import android.app.Activity;
//import android.net.Uri;
//import android.os.Bundle;
//import android.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import com.vik.babiesdayschedule.R;
//import com.vik.babiesdayschedule.models.SegmentType;
//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link AddActivityFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link AddActivityFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class AddActivityFragment extends SegmentEditFragment {
//
//    private View view;
//    private Button btn_save;
//    private Button btn_editFrom;
//    private Button btn_editTo;
//    private SegmentEditManager mSegmentEditManager;
//    long startTimeForDuration;
//    boolean isStartTimeChanged;
//    boolean isFinishTimeChanged;
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment AddActivityFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static AddActivityFragment newInstance(String param1, String param2) {
//        AddActivityFragment fragment = new AddActivityFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public AddActivityFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_add_activity, container, false);
//        mSegmentEditManager = new SegmentEditManager();
//
//        String[] data = getArrayOfSegmentType(); //{"1", "2", "3"};
//
//        btn_save = (Button) view.findViewById(R.id.btn_save);
//        btn_editFrom = (Button) view.findViewById(R.id.btn_edit_from_2);
//        btn_editTo = (Button) view.findViewById(R.id.btn_edit_to_2);
//
//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSegmentEditManager.onSave();
//            }
//        });
//
//
//
//        // адаптер
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, data );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
//        spinner.setAdapter(adapter);
//        // выделяем элемент
//        spinner.setSelection(0);
//        // устанавливаем обработчик нажатия
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                // показываем позиция нажатого элемента
//                //Toast.makeText(getActivity().getApplicationContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//            }
//        });
//        return view;
//    }
//
//    private String[] getArrayOfSegmentType() {
//        String [] arr = new String [SegmentType.values().length];
//        for(int i = 0; i < SegmentType.values().length; i++){
//            arr[i] = SegmentType.values()[i].toString();
//        }
//        return arr;
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }
//
//}
