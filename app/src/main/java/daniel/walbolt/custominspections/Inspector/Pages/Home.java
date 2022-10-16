package daniel.walbolt.custominspections.Inspector.Pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import daniel.walbolt.custominspections.Activities.ScheduleActivity;
import daniel.walbolt.custominspections.Adapters.ScheduleRecycler.InspectionScheduleRecyclerAdapter;
import daniel.walbolt.custominspections.Adapters.ScheduleRecycler.ScheduleRecyclerTouchHelper;
import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.Inspector.Pages.Scheduler;
import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;
import daniel.walbolt.custominspections.MainActivity;
import daniel.walbolt.custominspections.R;

public class Home
{

    //The home page is the first page that is loaded when starting the app.

    /*

    Home Page has the following features:

    Schedule an inspection using the Scheduler
    View scheduled inspections
    Start a scheduled inspection
    Remove a scheduled inspection
    View saved inspections

    Edit the overall theme of the app

     */

    //Schedules are obtained from the database every time this page is loaded.
    private ArrayList<Schedule> schedules;
    //Past inspections are NOT obtained upon loading, but ONLY upon searching an inputted date.
    private ArrayList<Schedule> pastInspections;

    //EditText fields for selecting the desired date
    private EditText year;
    private EditText month;
    private EditText day;

    //RecyclerViews to display the scheduled inspections and saved inspection files.
    private RecyclerView scheduleRecycler;
    private RecyclerView pastInspectionRecycler;

    public Home(Activity activity)
    {

        //Set content view
        activity.setContentView(R.layout.app_home);

        //Initialize the page
        initTheme(activity);

        //Initialize the buttons on the page
        initButtons(activity);

        //Initialize the text fields on the page
        initEditText(activity);
        //Get the schedules (inspections TO BE COMPLETED)
        schedules = new ArrayList<>();
        new FirebaseBusiness().loadSchedules(this);
        pastInspections = new ArrayList<>();

        //Initialize the scheduler that contains the scheduled inspections
        initSchedulesRecycler(activity);

        initPastInspectionRecycler(activity);


    }

    //Boiler plate method to get the current theme of the app
    private void initTheme(Activity activity) {

        //Find the theme switch object and set it according to shared preference
        TextView themeView = activity.findViewById(R.id.theme_switch);
        themeView.setText(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ? "Night Theme" : "Light Theme"); // Set the text of the theme button to display the current theme

        //Create a listener to change the theme if switched.
        themeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((TextView) v).getText() == "Light Theme") {
                    //If the theme is alright LIGHT, switch it to NIGHT
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    ((TextView) v).setText("Night Theme");
                    activity.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(MainActivity.NIGHT_MODE, true).apply();

                } else {

                    //If the theme is already NIGHT, switch it to LIGHT
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    ((TextView) v).setText("Light Theme");
                    activity.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(MainActivity.NIGHT_MODE, false).apply();


                }

            }

        });

    }

    public void updateScheduledRecycler(ArrayList<Schedule> schedules)
    {

        this.schedules.clear();
        this.schedules.addAll(schedules);
        sort(schedules);
        scheduleRecycler.getAdapter().notifyDataSetChanged();


    }

    public void updatePastInspectionRecycler()
    {

        sort(pastInspections);
        pastInspectionRecycler.getAdapter().notifyDataSetChanged();

    }

    //Initialize the edit text fields for selecting dates
    private void initEditText(Activity mActivity)
    {

        year = mActivity.findViewById(R.id.inspector_home_past_input_year);
        year.setOnFocusChangeListener(getFieldFocusChangeListener("Year"));
        month = mActivity.findViewById(R.id.inspector_home_past_input_month);
        month.setOnFocusChangeListener(getFieldFocusChangeListener("Month"));
        day = mActivity.findViewById(R.id.inspector_home_past_input_day);
        day.setOnFocusChangeListener(getFieldFocusChangeListener("Day"));

    }

    private void initPastInspectionRecycler(Activity mActivity)
    {

        pastInspectionRecycler = mActivity.findViewById(R.id.inspector_home_past_recycler);
        TextView emptyView = mActivity.findViewById(R.id.inspector_home_past_recycler_emptyView);
        InspectionScheduleRecyclerAdapter adapter = new InspectionScheduleRecyclerAdapter(pastInspections, mActivity, pastInspectionRecycler, emptyView, true);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        pastInspectionRecycler.setAdapter(adapter);
        pastInspectionRecycler.setLayoutManager(manager);

    }

    private void initButtons(final Activity mActivity)
    {

        Button scheduleNew = mActivity.findViewById(R.id.inspector_home_schedule_button);
        scheduleNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open a new activity for the Scheduler. Opening a new activity has a cool animation, and allows the BACK button to be functional!
                Intent scheduler = new Intent(mActivity, ScheduleActivity.class);
                mActivity.startActivity(scheduler);
            }
        });

        Button reloadSchedules = mActivity.findViewById(R.id.inspector_home_schedule_reload);
        reloadSchedules.setOnClickListener((v) -> {
            new FirebaseBusiness().loadSchedules(this);// Retrieve schedules
            Toast.makeText(mActivity, "Reloaded Schedules", Toast.LENGTH_SHORT);
        });

        Button searchPast = mActivity.findViewById(R.id.inspector_home_past_search);
        searchPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPastInspections(mActivity);
            }
        });

    }

    private void initSchedulesRecycler(Activity mActivity)
    {

        scheduleRecycler = mActivity.findViewById(R.id.inspector_home_scheduled_inspections_container);
        TextView emptyView = mActivity.findViewById(R.id.inspector_home_empty_schedules);
        InspectionScheduleRecyclerAdapter adapter = new InspectionScheduleRecyclerAdapter(schedules, mActivity, scheduleRecycler, emptyView, false);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);

        ItemTouchHelper.Callback callback = new ScheduleRecyclerTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        adapter.setTouchHelper(helper);
        helper.attachToRecyclerView(scheduleRecycler);

        scheduleRecycler.setAdapter(adapter);
        scheduleRecycler.setLayoutManager(manager);

    }

    private void sort(ArrayList<Schedule> schedules)
    {

        for(int i = 0; i < schedules.size(); i++)
        {

            for(int r = schedules.size()-1; r > 0; r--)
            {

                Schedule aSchedule = schedules.get(r);
                if(aSchedule.isSoonerThan(schedules.get(r-1)))
                {

                    Schedule laterSchedule = schedules.get(r-1);
                    schedules.set(r-1, aSchedule);
                    schedules.set(r, laterSchedule);

                }

            }

        }

    }

    private View.OnFocusChangeListener getFieldFocusChangeListener(final CharSequence originalHint)
    {

        //When the field is selected, make the hint disappear. THis is just a visual preference.
        //When the field is deselected, make the hint appear again (hint only shows with no text in the field)

        return (view, b) -> {
            if(b)
                ((EditText)view).setHint("");
            else {
                ((EditText) view).setHint(originalHint);
            }
        };

    }

    //This method is called by the SEARCH button.
    private void loadPastInspections(Activity mActivity)
    {

        //Create a calendar object to define the time-interval we are searching for  inspections in.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Set the search time to now by default.

        //Set the calendar year,month,and day based on the information inputted. Some of these might be empty--catch any errors.
        boolean searchEntireMonth = false;
        try
        {

            if(!year.getText().toString().isEmpty())
                calendar.set(Calendar.YEAR, Integer.parseInt(year.getText().toString()));

            //Get the month inputted. Humans consider January month #1, but java starts at 0.
            if(!month.getText().toString().isEmpty())
                calendar.set(Calendar.MONTH, Integer.parseInt(month.getText().toString())-1);

            if(!day.getText().toString().isEmpty())
            {

                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.getText().toString())); // Set the day equal to a value distinguishable to be invalid (every inspection of that month should show)

            }
            else
                searchEntireMonth = true;

            year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            month.setText(String.valueOf(calendar.get(Calendar.MONTH)+1));

        }
        catch(Exception e)
        {

            Toast.makeText(mActivity, "Error reading date!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }


        // Make the time the very beginning of any day. This enables every inspection to load, despite their start time, as long as their days, months, and years are requested.
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //Retrieve the database saved inspections using the calendar information
        FirebaseBusiness db = new FirebaseBusiness();
        pastInspections.clear();
        updatePastInspectionRecycler();
        db.loadPastInspections(calendar, searchEntireMonth, pastInspections, this);

    }

}