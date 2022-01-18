package daniel.walbolt.custominspections.Inspector.Pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import daniel.walbolt.custominspections.Activities.PDFActivity;
import daniel.walbolt.custominspections.Adapters.InspectionSystemAdapter;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.SystemsDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.ClientDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.UploadDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Inspection;
import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.MainActivity;
import daniel.walbolt.custominspections.R;

public class Main {

    /*

    The Main page is the first page loaded when viewing/starting an inspection.
    This page contains:

    The inspection systems and their statuses
    Generate PDF option
    Upload data to database option
    View client information option
    Add system option
    Delete system option

     */

    private InspectionSystemAdapter systemListAdapter;

    public static Schedule inspectionSchedule;

    public Main(final Activity activity, Schedule schedule)
    {

        activity.setContentView(R.layout.inspection_front);
        inspectionSchedule = schedule;

        //The main page is only opened when an inspection is started, whether it be a past one or new one.
        //Create a new inspection object, and load the default systems.
        if(schedule.inspection == null)
        {

            schedule.inspection = new Inspection();
            schedule.inspection.loadDefaultSystems(activity);

        }


        //Initialize the recycler view for the MainSystem list.
        initRecyclerView(activity, schedule.inspection);

        //Initialize the page's buttons.
        initButtons(activity, schedule);

        //Initialize the page
        initTheme(activity);

        if(inspectionSchedule.isPastInspection)
        {

            if(!schedule.inspection.hasLoaded) {
                schedule.inspection.hasLoaded = true;
                schedule.inspection.loadPastInspection(activity, this);
            }

        }
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

    private void initButtons(final Activity activity, final Schedule inspectionSchedule)
    {

        Button lostSystems = activity.findViewById(R.id.main_create_system);
        lostSystems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SystemsDialog(activity, systemListAdapter, inspectionSchedule.inspection.getSystemList(), false,null);
            }
        });

        TextView pdf = activity.findViewById(R.id.inspection_main_pdf);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pdfPreview = new Intent(activity, PDFActivity.class);
                activity.startActivity(pdfPreview);
            }
        });

        TextView client = activity.findViewById(R.id.inspection_main_address);
        client.setText(inspectionSchedule.address);
        //Client information is NOT saved in an inspection file. This is for privacy reasons,
        // but a future feature might allow for frequent clients to save their information with us.
        if(!inspectionSchedule.isPastInspection)
            client.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ClientDialog(activity, inspectionSchedule);
                }
            });
        else
            client.setVisibility(View.GONE);

        Button upload = activity.findViewById(R.id.inspection_main_upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadDialog(activity);
            }
        });

    }

    private void initRecyclerView(Activity mActivity, Inspection inspection)
    {

        RecyclerView systemList = mActivity.findViewById(R.id.inspection_main_systems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        TextView emptyView = mActivity.findViewById(R.id.inspection_main_systems_emptyView);

        systemListAdapter = new InspectionSystemAdapter(systemList, emptyView, inspection.getSystemList());

        systemList.setAdapter(systemListAdapter);
        systemList.setLayoutManager(linearLayoutManager);

    }

    public void updateSystemList()
    {

        systemListAdapter.notifyDataSetChanged();

    }

}
