package daniel.walbolt.custominspections.Inspector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
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
    //Past inspections are NOT obtained upon loading, but ONLY upon searching a date.
    private ArrayList<Schedule> pastInspections;

    private SwitchCompat themeSelect;

    public Home(Activity activity)
    {

        //Set content view
        activity.setContentView(R.layout.app_home);

        //Retrieve database object

        //Initialize the page
        initTheme(activity);

    }

    private void initTheme(Activity activity)
    {

        //Find the theme switch object and set it according to shared preference
        themeSelect = activity.findViewById(R.id.home_theme_switch);
        themeSelect.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        //Create a listener to change the theme if switched.
        themeSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(MainActivity.NIGHT_MODE, isChecked).apply();
                if(isChecked)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

    }

}