package daniel.walbolt.custominspections.Inspector;

import android.app.Activity;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
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

    public Home(Activity activity)
    {

        //Set content view

        //Retrieve database object

        //Initialize the page

    }

}
