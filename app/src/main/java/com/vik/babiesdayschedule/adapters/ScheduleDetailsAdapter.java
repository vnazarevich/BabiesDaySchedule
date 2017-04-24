package com.vik.babiesdayschedule.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vik.babiesdayschedule.DataManager;
import com.vik.babiesdayschedule.R;
import com.vik.babiesdayschedule.activities.MainActivity;
import com.vik.babiesdayschedule.models.Schedule;
import com.vik.babiesdayschedule.models.Segment;
import com.vik.babiesdayschedule.observerStrategy.ScheduleObservableImp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 08.01.2017.
 */
public class ScheduleDetailsAdapter  extends ArrayAdapter{
    private  Context context;
    private List <Segment> segments = new ArrayList();
    private Schedule schedule;
    private View view;
    private ScheduleObservableImp observable;
    private final static String TAG2 = "SegmentEditFragment";

    public ScheduleDetailsAdapter(Context context, int resource, List<Segment> segments, Schedule schedule) {
        super(context, resource, segments);
        this.context = context;
        this.segments = segments;
        this.schedule = schedule;
        observable = DataManager.getInstance().getObservable();
        Log.w("+++ ScheduleDetailsAdapter", " --- constructor ---");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        view = convertView;

        if(view == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            view = inflater.inflate(R.layout.detail_list_item, null);
        }
        TextView textView = (TextView)view.findViewById(R.id.list_item_string);
        textView.setText(segments.get(position).toString());
        ImageButton editBtn = (ImageButton) view.findViewById(R.id.edit_btn);
        ImageButton deleteBtn = (ImageButton) view.findViewById(R.id.delete_btn);

        //TODO check edit only current list segments
        //TODO how to say Activity call EditFragment?
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schedule.setDetailsSegment(segments.get(position));
                DataManager.getInstance().setScheduleForDetailsFragment(schedule);
                ((MainActivity)context).initializeFragments(TAG2);
                //observable.notifyScheduleObserver();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Segment removeSegment = segments.remove(position);
                schedule.getSegmentsInProgress().remove(removeSegment);
                observable.notifyScheduleObserver();
            }
        });
        return view;
    }
}
