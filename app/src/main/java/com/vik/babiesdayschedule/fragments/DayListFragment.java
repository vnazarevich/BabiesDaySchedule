package com.vik.babiesdayschedule.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.vik.babiesdayschedule.DataManager;
import com.vik.babiesdayschedule.R;
import com.vik.babiesdayschedule.Settings;
import com.vik.babiesdayschedule.UsersSettings;
import com.vik.babiesdayschedule.adapters.ScheduleDetailsAdapter;
import com.vik.babiesdayschedule.models.Schedule;
import com.vik.babiesdayschedule.models.Segment;
import com.vik.babiesdayschedule.observerStrategy.ScheduleObserver;

import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.vik.babiesdayschedule.fragments.DayListFragment.OnDayListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayListFragment extends Fragment implements ScheduleObserver {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final static String TAG2 = "SegmentEditFragment";
    private ArrayAdapter detailsListAdapter;
    private ListView detailsListView;
    private Calendar date;
    private DataManager dataManager;
    private int year, month, day;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnDayListFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DayListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DayListFragment newInstance(String param1, String param2) {
        DayListFragment fragment = new DayListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DayListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        registerAsObserver();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day_list, container, false);
        dataManager = DataManager.getInstance();
        Schedule schedule = dataManager.getScheduleForDetailsFragment(); //!
        List<Segment> segments = schedule.getSegments();
        date = schedule.getCalendar();
        detailsListAdapter = new ScheduleDetailsAdapter(getActivity(), R.layout.detail_list_layout, segments, schedule);
        detailsListView = (ListView) view.findViewById(R.id.listView);
        final View hiderView = inflater.inflate(R.layout.list_header, null);
        ((TextView) hiderView.findViewById(R.id.textView)).setText(String.format("%td %tB %tY", date, date, date));
        detailsListView.addHeaderView(hiderView, schedule, true);

        detailsListView.setAdapter(detailsListAdapter);
        Log.i("+++ DayListFragment ", "onCreateView(),  data = " + String.format("%td %tB %tY", date, date, date));
        hiderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                ((TextView) hiderView.findViewById(R.id.textView)).setText(String.format("%td %tB %tY", calendar, calendar, calendar));

                                Schedule schedule = dataManager.getSchedule(calendar);
                                detailsListAdapter = new ScheduleDetailsAdapter(getActivity(), R.layout.detail_list_layout, schedule.getSegments(), schedule );
                                detailsListView.setAdapter(detailsListAdapter);
                                UsersSettings.setDateSelectedByUser(calendar);
                                dataManager.setScheduleForDetailsFragment(calendar);
                                dataManager.getObservable().notifyScheduleObserver();
                            }
                        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDayListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void refresh() {

        Log.w("DayListFragment +++ ", "refresh");
        if (null != detailsListAdapter) {
            detailsListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void registerAsObserver() {
        DataManager.getInstance().getObservable().addScheduleObserver(this);
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
    public interface OnDayListFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

//    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        SegmentEditFragment segmentEditFragment = new SegmentEditFragment();
//        fragmentTransaction.replace(R.id.fragment_container, segmentEditFragment);
//        fragmentTransaction.addToBackStack(TAG2);
//    }
}
