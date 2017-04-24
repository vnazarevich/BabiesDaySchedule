package com.vik.babiesdayschedule.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vik.babiesdayschedule.DataManager;
import com.vik.babiesdayschedule.R;
import com.vik.babiesdayschedule.Settings;
import com.vik.babiesdayschedule.UsersSettings;
import com.vik.babiesdayschedule.fragments.AddActivityFragment;
import com.vik.babiesdayschedule.fragments.DayListFragment;
import com.vik.babiesdayschedule.fragments.SegmentEditFragment;
import com.vik.babiesdayschedule.models.Schedule;
import com.vik.babiesdayschedule.models.Segment;
import com.vik.babiesdayschedule.models.SegmentType;
import com.vik.babiesdayschedule.observerStrategy.ScheduleObserver;
import com.vik.babiesdayschedule.wiget.ScheduleDraw;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity implements ScheduleObserver, DayListFragment.OnDayListFragmentInteractionListener, SegmentEditFragment.OnSegmentEditFragmentListener, AddActivityFragment.OnAddActivityFragmentInteractionListener {
    private final static String TAG1 = "DayListFragment";
    private final static String TAG2 = "SegmentEditFragment";
    private final static String TAG3 = "CleanFragments";
    private final static String TAG4 = "AddActivityFragment";

    private FragmentTransaction fragmentTransaction;
    private DayListFragment dayListFragment;
    private SegmentEditFragment segmentEditFragment;
    private AddActivityFragment addActivityFragment;

    private CheckBox isPee;
    private CheckBox isPoo;
    private ScheduleDraw view_schedule;
    private ImageButton btn_sleep;
    private ImageButton btn_eat;
    private ImageButton btn_diaper;
    private ImageButton btn_addActivity;
    private TextView text_statisticSleep;
    private TextView text_statisticEat;
    private TextView text_statisticDiaper;
    private Button btn_clearSchedule;
    private Button btn_dayDetails;
    private boolean isBabySleep = false;
    private boolean isBabyEat = false;
    private boolean isBabyActive = false;
    private Map<ImageButton, Boolean> btn_states;

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Settings.setRESOURCES(getResources());
        dataManager = DataManager.getInstance();
        DataManager.loadDataFromPreferences(this);
        registerAsObserver();
        initializeComponents();
        initializeLastActivityState();
        initializeFragments(TAG1);
        fillComponents();
        Log.w("+++ MainActivity ", "onCreate()");

    }

    private void initializeComponents() {
        text_statisticSleep = (TextView) findViewById(R.id.text_statistic_sleep);
        text_statisticEat = (TextView) findViewById(R.id.text_statistic_eat);
        text_statisticDiaper = (TextView) findViewById(R.id.text_statistic_diaper);

        btn_sleep = (ImageButton) findViewById(R.id.sleep);
        btn_eat = (ImageButton) findViewById(R.id.eat);
        btn_diaper = (ImageButton) findViewById(R.id.diaper);
        btn_addActivity = (ImageButton) findViewById(R.id.btn_addActivity);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btn_states= new HashMap<ImageButton, Boolean>(){{
            put (btn_sleep, isBabySleep);
            put (btn_eat, isBabyEat);
        }};

        view_schedule = (ScheduleDraw) findViewById(R.id.schedule);
        btn_clearSchedule = (Button) findViewById(R.id.btn_clearSchedule);
        btn_dayDetails = (Button) findViewById(R.id.btn_day_details);

        btn_addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeFragments(TAG4);
            }
        });

        btn_dayDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeFragments(TAG1);
            }
        });

        btn_clearSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().getSchedules().remove(DataManager.getInstance().getCurrentSchedule());
                setInitialStateOfActivity();
                DataManager.getInstance().getObservable().notifyScheduleObserver();
                Log.w("+++", "Clear schedule");
            }
        });
        btn_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Baby wakes up
                if (isBabySleep) {
                    finishSleep();
                    Log.i("+++", "Baby wakes up");
                }
                // Baby start sleeping
                else {
                    startSleep();
                    Log.i("+++", "Baby start sleep");
                }
                DataManager.getInstance().getObservable().notifyScheduleObserver();
            }
        });

        btn_eat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // baby finish eating
                if (isBabyEat) {finishEat();}
                //baby start eating
                else {startEat();}
                DataManager.getInstance().getObservable().notifyScheduleObserver();
            }
        });

        btn_diaper.setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Segment segment = dataManager.getCurrentSchedule().startCreatingSegment(SegmentType.DIAPER);
                                segment.setPooPee(isPoo.isChecked(), isPee.isChecked());
                                DataManager.getInstance().getObservable().notifyScheduleObserver();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                View view = getLayoutInflater().inflate(R.layout.short_activity_dialog,null);
                isPee = (CheckBox) view.findViewById(R.id.isPee);
                isPoo = (CheckBox) view.findViewById(R.id.isPoo);
                builder.setView(view).setPositiveButton("Yes", dialogClickListener).setNegativeButton("Cancel", dialogClickListener).show();

//                DataManager.getInstance().getObservable().notifyScheduleObserver();
            }
        });
    }

    private void fillComponents() {
        Calendar date = UsersSettings.getDateSelectedByUser();

        text_statisticSleep.setText(getResources().getString(R.string.sleep) + " " + dataManager.getSchedule(date).getStatistic().getSleepSum_shortString());
        text_statisticEat.setText(getResources().getString(R.string.eat) + " " + dataManager.getSchedule(date).getStatistic().getEatSum_shortString());
        text_statisticDiaper.setText(getResources().getString(R.string.diaper) + " " + dataManager.getSchedule(date).getStatistic().getDiaperSum_shortString());
    }
    /**
     * Find current action and make:
     * - action`s buttons Edit and Delete - VISIBLE
     * - set isBaby***=true
     */
    private void initializeLastActivityState() {
        if (dataManager.getCurrentSchedule() != null && dataManager.getCurrentSchedule().getSegmentsInProgress() != null) {
            for (Segment segment : dataManager.getCurrentSchedule().getSegmentsInProgress()) {
                switch (segment.getType()) {
                    case EAT:
                        btn_eat.setBackgroundColor(getResources().getColor(R.color.light_blue));
                        isBabyEat = true;

//                    btn_eatDetails.setVisibility(View.VISIBLE);
//                    btn_eatCancel.setVisibility(View.VISIBLE);
                        break;
                    case SLEEP:
                        btn_sleep.setBackgroundColor(getResources().getColor(R.color.purple));
                        isBabySleep = true;
//                    btn_sleepDetails.setVisibility(View.VISIBLE);
//                    btn_sleepCancel.setVisibility(View.VISIBLE);
                        break;
                    case DIAPER:
                        btn_diaper.setBackgroundColor(getResources().getColor(R.color.light_green));
                        isBabyActive = true;
//                    btn_activeDetails.setVisibility(View.VISIBLE);
//                    btn_activeCancel.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

//    }

    public void initializeFragments(String tag) {
        fragmentTransaction = getFragmentManager().beginTransaction();
        switch (tag) {
            case "DayListFragment":
                dayListFragment = new DayListFragment();
                fragmentTransaction.replace(R.id.fragment_container, dayListFragment);
                fragmentTransaction.addToBackStack(tag);
                break;
            case "SegmentEditFragment":
                segmentEditFragment = new SegmentEditFragment();
                fragmentTransaction.replace(R.id.fragment_container, segmentEditFragment);
                fragmentTransaction.addToBackStack(tag);
                break;
            case "AddActivityFragment":
                addActivityFragment = new AddActivityFragment();
                fragmentTransaction.replace(R.id.fragment_container, addActivityFragment);
               // fragmentTransaction.addToBackStack(tag);
                break;

            //TODO check if it good to use new Bundle()
            case "CleanFragments":
                // onCreate(new Bundle());
                //clearBackStack();
//                if (segmentEditFragment != null) {
//                    fragmentTransaction.remove(segmentEditFragment);
//                }
//                if (dayListFragment != null) {
//                    fragmentTransaction.remove(dayListFragment);
//                }
                clearBackStack();
        }
        fragmentTransaction.commit();
    }

    private void setInitialStateOfActivity() {
        clearBackStack();

        if (isBabySleep) {
            isBabySleep = false;
//            btn_sleepDetails.setVisibility(View.INVISIBLE);
//            btn_sleepCancel.setVisibility(View.INVISIBLE);
            btn_sleep.setBackgroundColor(getResources().getColor(R.color.grey));
        }
        if (isBabyEat) {
            isBabyEat = false;
//            btn_eatDetails.setVisibility(View.INVISIBLE);
//            btn_eatCancel.setVisibility(View.INVISIBLE);
            btn_eat.setBackgroundColor(getResources().getColor(R.color.grey));
        }
        if (isBabyActive) {
            isBabyActive = false;
//            btn_activeDetails.setVisibility(View.INVISIBLE);
//            btn_activeCancel.setVisibility(View.INVISIBLE);
            btn_diaper.setBackgroundColor(getResources().getColor(R.color.grey));
        }
    }

    private void startEat() {
        isBabyEat = true;
//        btn_eatDetails.setVisibility(View.VISIBLE);
//        btn_eatCancel.setVisibility(View.VISIBLE);
        btn_eat.setBackgroundColor(getResources().getColor(R.color.light_blue));
        dataManager.getCurrentSchedule().startCreatingSegment(SegmentType.EAT);
        //view_schedule.getSchedule().startCreatingSegment(SegmentType.EAT);
        Log.i("+++", "Baby start eating");
    }

    private void startSleep() {
        isBabySleep = true;
//        btn_sleepDetails.setVisibility(View.VISIBLE);
//        btn_sleepCancel.setVisibility(View.VISIBLE);
        btn_sleep.setBackgroundColor(getResources().getColor(R.color.purple));
        dataManager.getCurrentSchedule().startCreatingSegment(SegmentType.SLEEP);
//        view_schedule.getSchedule().startCreatingSegment(SegmentType.SLEEP);
    }

    private void startActive() {
        isBabyActive = true;
//        btn_activeDetails.setVisibility(View.VISIBLE);
//        btn_activeCancel.setVisibility(View.VISIBLE);
        btn_diaper.setBackgroundColor(getResources().getColor(R.color.light_green));
        dataManager.getCurrentSchedule().startCreatingSegment(SegmentType.DIAPER);
//        view_schedule.getSchedule().startCreatingSegment(SegmentType.DIAPER);
    }

    private void finishEat() {
        Log.i("+++", "Baby finish eating");
        if (isBabyEat) {
            isBabyEat = false;
//            btn_eatDetails.setVisibility(View.INVISIBLE);
//            btn_eatCancel.setVisibility(View.INVISIBLE);
            btn_eat.setBackgroundColor(getResources().getColor(R.color.grey));
            dataManager.getCurrentSchedule().finishCreatingSegment(SegmentType.EAT);
//            view_schedule.getSchedule().finishCreatingSegment();
        }
    }

    private void finishActive() {
//        if (isBabyActive) {
//            isBabyActive = false;
////            btn_activeDetails.setVisibility(View.INVISIBLE);
////            btn_activeCancel.setVisibility(View.INVISIBLE);
//            btn_diaper.setBackgroundColor(getResources().getColor(R.color.grey));
//            dataManager.getCurrentSchedule().finishCreatingSegment();
//            //view_schedule.getSchedule().finishCreatingSegment();
//        }
    }

    private void finishSleep() {
        if (isBabySleep) {
            isBabySleep = false;
//            btn_sleepDetails.setVisibility(View.INVISIBLE);
//            btn_sleepCancel.setVisibility(View.INVISIBLE);
            btn_sleep.setBackgroundColor(getResources().getColor(R.color.grey));
            dataManager.getCurrentSchedule().finishCreatingSegment(SegmentType.SLEEP);
//            view_schedule.getSchedule().finishCreatingSegment();
        }
    }

    private void setDrawable() {
        // imgView.setImageResource(R.drawable.shape);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks   on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh() {
        //TODO забрати detailsListAdapter.notifyDataSetChanged();
        view_schedule.invalidate();
        deactivateButtons();
        initializeLastActivityState();
        fillComponents();
        Log.i("+++ MainActivity ", "was refresh");
    }

    private void deactivateButtons() {
        for(ImageButton btn: btn_states.keySet()) {
            btn.setBackgroundColor(getResources().getColor(R.color.grey));
        }
        isBabyEat = false;
        isBabyActive = false;
        isBabySleep = false;

    }

    @Override
    public void registerAsObserver() {
        DataManager.getInstance().getObservable().addScheduleObserver(this);
    }

    //TODO fill this method, це слухач DayListFragment і SegmentEditFragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        DataManager.saveDataToSharedPreference(this);
    }

    private void clearBackStack() {
//        FragmentManager manager = getFragmentManager();
//        FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
//        manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
