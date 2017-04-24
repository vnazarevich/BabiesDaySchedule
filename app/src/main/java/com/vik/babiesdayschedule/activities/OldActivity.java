package com.vik.babiesdayschedule.activities;

/**
* Created by User on 05.02.2017.
*/
public class OldActivity {}

//    package com.vik.babiesdayschedule.activities;
//
//    import android.app.Activity;
//    import android.app.FragmentManager;
//    import android.app.FragmentTransaction;
//    import android.net.Uri;
//    import android.os.Bundle;
//    import android.util.Log;
//    import android.view.Menu;
//    import android.view.MenuItem;
//    import android.view.View;
//    import android.widget.Button;
//    import android.widget.ImageButton;
//
//    import com.vik.babiesdayschedule.DataManager;
//    import com.vik.babiesdayschedule.R;
//    import com.vik.babiesdayschedule.fragments.DayListFragment;
//    import com.vik.babiesdayschedule.fragments.SegmentEditFragment;
//    import com.vik.babiesdayschedule.models.Schedule;
//    import com.vik.babiesdayschedule.models.SegmentType;
//    import com.vik.babiesdayschedule.observerStrategy.ScheduleObserver;
//    import com.vik.babiesdayschedule.wiget.ScheduleDraw;
//
//
//    public class MainActivity extends Activity implements ScheduleObserver, DayListFragment.OnDayListFragmentInteractionListener, SegmentEditFragment.OnSegmentEditFragmentListener {
//        private final static String TAG1 = "DayListFragment";
//        private final static String TAG2 = "SegmentEditFragment";
//        private final static String TAG3 = "CleanFragments";
//
//        private FragmentTransaction fragmentTransaction;
//        private DayListFragment dayListFragment;
//        private SegmentEditFragment segmentEditFragment;
//
//        private ScheduleDraw view_schedule;
//        private ImageButton btn_sleep;
//        private ImageButton btn_eat;
//        private ImageButton btn_active;
//        private ImageButton btn_sleepDetails;
//        private ImageButton btn_sleepCancel;
//        private ImageButton btn_eatDetails;
//        private ImageButton btn_eatCancel;
//        private ImageButton btn_activeDetails;
//        private ImageButton btn_activeCancel;
//        private Button btn_clearSchedule;
//        private Button btn_dayDetails;
//        private boolean isBabySleep = false;
//        private boolean isBabyEat = false;
//        private boolean isBabyActive = false;
//
//        private DataManager dataManager;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_main);
//            dataManager = DataManager.getInstance();
//            DataManager.loadDataFromPreferences(this);
//            registerAsObserver();
//            initializeComponents();
//            initializeLastActivityState();
//            Log.w("+++ MainActivity ", "onCreate()");
//
//        }
//
//        /**
//         * Find current action and make:
//         * - action`s buttons Edit and Delete - VISIBLE
//         * - set isBaby***=true
//         */
//        private void initializeLastActivityState() {
//            if (dataManager.getCurrentSchedule() != null  && dataManager.getCurrentSchedule().getCurrentSegment() !=null ) {
//                switch (dataManager.getCurrentSchedule().getCurrentSegment().getType()) {
//                    case EAT:
//                        btn_eat.setBackgroundColor(getResources().getColor(R.color.light_blue));
//                        btn_eatDetails.setVisibility(View.VISIBLE);
//                        btn_eatCancel.setVisibility(View.VISIBLE);
//                        isBabyEat = true;
//                        break;
//                    case SLEEP:
//                        btn_sleep.setBackgroundColor(getResources().getColor(R.color.purple));
//                        btn_sleepDetails.setVisibility(View.VISIBLE);
//                        btn_sleepCancel.setVisibility(View.VISIBLE);
//                        isBabySleep = true;
//                        break;
//                    case DIAPER:
//                        btn_active.setBackgroundColor(getResources().getColor(R.color.light_green));
//                        btn_activeDetails.setVisibility(View.VISIBLE);
//                        btn_activeCancel.setVisibility(View.VISIBLE);
//                        isBabyActive = true;
//                        break;
//                }
//            }
//        }
////
////    private void loadData() {
////        DataManager.setSchedules(DataManager.getInstance().saveDataToPreferences(this));
////        Log.w("MainActivity loadData +++ " , "" );
////        DataManager.getInstance().initialization();
////    }
//
//        public void initializeFragments(String tag) {
//            fragmentTransaction = getFragmentManager().beginTransaction();
//            switch (tag) {
//                case "DayListFragment":
//                    dayListFragment = new DayListFragment();
//                    fragmentTransaction.replace(R.id.fragment_container, dayListFragment);
//                    fragmentTransaction.addToBackStack(tag);
//                    break;
//                case "SegmentEditFragment":
//                    segmentEditFragment = new SegmentEditFragment();
//                    fragmentTransaction.replace(R.id.fragment_container, segmentEditFragment);
//                    fragmentTransaction.addToBackStack(tag);
//                    break;
//
//                //TODO check if it good to use new Bundle()
//                case "CleanFragments":
//                    // onCreate(new Bundle());
//                    //clearBackStack();
////                if (segmentEditFragment != null) {
////                    fragmentTransaction.remove(segmentEditFragment);
////                }
////                if (dayListFragment != null) {
////                    fragmentTransaction.remove(dayListFragment);
////                }
//                    clearBackStack();
//            }
//            fragmentTransaction.commit();
//        }
//
//        private void initializeComponents() {
//            btn_sleep = (ImageButton) findViewById(R.id.sleep);
//            btn_eat = (ImageButton) findViewById(R.id.eat);
//            btn_active = (ImageButton) findViewById(R.id.active);
//
//            view_schedule = (ScheduleDraw) findViewById(R.id.schedule);
//
//            btn_sleepDetails = (ImageButton) findViewById(R.id.sleepDetails);
//            btn_sleepCancel = (ImageButton) findViewById(R.id.sleepCancel);
//            btn_eatDetails = (ImageButton) findViewById(R.id.eatDetails);
//            btn_eatCancel = (ImageButton) findViewById(R.id.eatCancel);
//            btn_activeDetails = (ImageButton) findViewById(R.id.activeDetails);
//            btn_activeCancel = (ImageButton) findViewById(R.id.activeCancel);
//            btn_clearSchedule = (Button) findViewById(R.id.btn_clearSchedule);
//            btn_dayDetails = (Button) findViewById(R.id.btn_day_details);
//
//            btn_sleepDetails.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Schedule schedule = DataManager.getInstance().getCurrentSchedule();
//                    schedule.setDetailsSegment(schedule.getCurrentSegment());
//                    initializeFragments(TAG2);
//                }
//            });
//            btn_eatDetails.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Schedule schedule = DataManager.getInstance().getCurrentSchedule();
//                    schedule.setDetailsSegment(schedule.getCurrentSegment());
//                    initializeFragments(TAG2);
//                }
//            });
//            btn_activeDetails.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Schedule schedule = DataManager.getInstance().getCurrentSchedule();
//                    schedule.setDetailsSegment(schedule.getCurrentSegment());
//                    initializeFragments(TAG2);
//                }
//            });
//            btn_dayDetails.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    initializeFragments(TAG1);
//                }
//            });
//
//            btn_clearSchedule.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DataManager.getInstance().getCurrentSchedule().clearSegments();
//                    DataManager.getInstance().getCurrentSchedule().setCurrentSegment(null);
//                    setInitialStateOfActivity();
//                    //initializeFragments(TAG3);
//                    DataManager.getInstance().getObservable().notifyScheduleObserver();
//                    Log.w("+++", "Clear schedule");
//                }
//            });
//            btn_sleep.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // Baby wakes up
//                    if (isBabySleep) {
//                        finishSleep();
//                        // startActive();
//                        Log.i("+++", "Baby wakes up");
//                    }
//                    // Baby start sleeping
//                    else {
////                    finishActive();
////                    finishEat();
//                        startSleep();
//                        Log.i("+++", "Baby start sleep");
//                    }
//                    DataManager.getInstance().getObservable().notifyScheduleObserver();
//                }
//            });
//
//            btn_eat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // baby finish eating
//                    if (isBabyEat) {
//                        finishEat();
//                        // startActive();
//                        Log.i("+++", "Baby finish eating");
//                    }
//                    //baby start eating
//                    else {
////                    finishActive();
////                    finishSleep();
//                        startEat();
//                        Log.i("+++", "Baby start eating");
//                    }
//                    DataManager.getInstance().getObservable().notifyScheduleObserver();
//                }
//            });
//
//            btn_active.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    // baby finish be active
//                    if (isBabyActive) {
//                        finishActive();
//                        finishEat();
//                        startSleep();
//                    } else {
//                        finishEat();
//                        finishSleep();
//                        startActive();
//                    }
//                    DataManager.getInstance().getObservable().notifyScheduleObserver();
//                }
//            });
//        }
//
//        private void setInitialStateOfActivity() {
//            clearBackStack();
//
//            if (isBabySleep) {
//                isBabySleep = false;
//                btn_sleepDetails.setVisibility(View.INVISIBLE);
//                btn_sleepCancel.setVisibility(View.INVISIBLE);
//                btn_sleep.setBackgroundColor(getResources().getColor(R.color.grey));
//            }
//            if (isBabyEat) {
//                isBabyEat = false;
//                btn_eatDetails.setVisibility(View.INVISIBLE);
//                btn_eatCancel.setVisibility(View.INVISIBLE);
//                btn_eat.setBackgroundColor(getResources().getColor(R.color.grey));
//            }
//            if (isBabyActive) {
//                isBabyActive = false;
//                btn_activeDetails.setVisibility(View.INVISIBLE);
//                btn_activeCancel.setVisibility(View.INVISIBLE);
//                btn_active.setBackgroundColor(getResources().getColor(R.color.grey));
//            }
//        }
//
//        private void startEat() {
//            isBabyEat = true;
//            btn_eatDetails.setVisibility(View.VISIBLE);
//            btn_eatCancel.setVisibility(View.VISIBLE);
//            btn_eat.setBackgroundColor(getResources().getColor(R.color.light_blue));
//            dataManager.getCurrentSchedule().startCreatingSegment(SegmentType.EAT);
//            //view_schedule.getSchedule().startCreatingSegment(SegmentType.EAT);
//        }
//
//        private void startSleep() {
//            isBabySleep = true;
//            btn_sleepDetails.setVisibility(View.VISIBLE);
//            btn_sleepCancel.setVisibility(View.VISIBLE);
//            btn_sleep.setBackgroundColor(getResources().getColor(R.color.purple));
//            dataManager.getCurrentSchedule().startCreatingSegment(SegmentType.SLEEP);
////        view_schedule.getSchedule().startCreatingSegment(SegmentType.SLEEP);
//        }
//
//        private void startActive() {
//            isBabyActive = true;
//            btn_activeDetails.setVisibility(View.VISIBLE);
//            btn_activeCancel.setVisibility(View.VISIBLE);
//            btn_active.setBackgroundColor(getResources().getColor(R.color.light_green));
//            dataManager.getCurrentSchedule().startCreatingSegment(SegmentType.DIAPER);
////        view_schedule.getSchedule().startCreatingSegment(SegmentType.DIAPER);
//        }
//
//        private void finishEat() {
//            if (isBabyEat) {
//                isBabyEat = false;
//                btn_eatDetails.setVisibility(View.INVISIBLE);
//                btn_eatCancel.setVisibility(View.INVISIBLE);
//                btn_eat.setBackgroundColor(getResources().getColor(R.color.grey));
//                dataManager.getCurrentSchedule().finishCreatingSegment();
////            view_schedule.getSchedule().finishCreatingSegment();
//            }
//        }
//
//        private void finishActive() {
//            if (isBabyActive) {
//                isBabyActive = false;
//                btn_activeDetails.setVisibility(View.INVISIBLE);
//                btn_activeCancel.setVisibility(View.INVISIBLE);
//                btn_active.setBackgroundColor(getResources().getColor(R.color.grey));
//                dataManager.getCurrentSchedule().finishCreatingSegment();
//                //view_schedule.getSchedule().finishCreatingSegment();
//            }
//        }
//
//        private void finishSleep() {
//            if (isBabySleep) {
//                isBabySleep = false;
//                btn_sleepDetails.setVisibility(View.INVISIBLE);
//                btn_sleepCancel.setVisibility(View.INVISIBLE);
//                btn_sleep.setBackgroundColor(getResources().getColor(R.color.grey));
//                dataManager.getCurrentSchedule().finishCreatingSegment();
////            view_schedule.getSchedule().finishCreatingSegment();
//            }
//        }
//
//        private void setDrawable() {
//            // imgView.setImageResource(R.drawable.shape);
//        }
//
//        @Override
//        public boolean onCreateOptionsMenu(Menu menu) {
//            // Inflate the menu; this adds items to the action bar if it is present.
//            getMenuInflater().inflate(R.menu.menu_main, menu);
//            return true;
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            // Handle action bar item clicks here. The action bar will
//            // automatically handle clicks   on the Home/Up button, so long
//            // as you specify a parent activity in AndroidManifest.xml.
//            int id = item.getItemId();
//
//            //noinspection SimplifiableIfStatement
//            if (id == R.id.action_settings) {
//                return true;
//            }
//
//            return super.onOptionsItemSelected(item);
//        }
//
//        @Override
//        public void refresh() {
//            //TODO забрати detailsListAdapter.notifyDataSetChanged();
//            view_schedule.invalidate();
////        detailsListAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void registerAsObserver() {
//            DataManager.getInstance().getObservable().addScheduleObserver(this);
//        }
//
//        //TODO fill this method, це слухач DayListFragment і SegmentEditFragment
//        @Override
//        public void onFragmentInteraction(Uri uri) {
//
//        }
//
//        @Override
//        protected void onStop() {
//            super.onStop();
//            DataManager.saveDataToSharedPreference(this);
//        }
//
//        private void clearBackStack() {
////        FragmentManager manager = getFragmentManager();
////        FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
////        manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            FragmentManager manager = getFragmentManager();
//            if (manager.getBackStackEntryCount() > 0) {
//                FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
//                manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            }
//        }
//    }
//
//}
