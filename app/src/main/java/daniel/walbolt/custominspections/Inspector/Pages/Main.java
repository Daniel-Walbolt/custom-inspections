package daniel.walbolt.custominspections.Inspector.Pages;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Activities.PDFActivity;
import daniel.walbolt.custominspections.Adapters.InspectionSystemAdapter;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ErrorAlert;
import daniel.walbolt.custominspections.Inspector.Dialogs.Creators.SystemsDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.ClientDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.UploadDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Inspection;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MajorComponent;
import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.Inspector.Objects.System;
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


     */

    private RecyclerView systemList;

    public static Schedule inspectionSchedule;

    public Main(final Activity activity, Schedule schedule)
    {

        activity.setContentView(R.layout.inspection_front);
        inspectionSchedule = schedule;

        //The main page is only opened when an inspection is started, whether it be a past one or new one.
        //Create a new inspection object, and load the default systems.
        if(schedule.inspection == null)
        {

            schedule.inspection = new Inspection(activity);
            schedule.inspection.loadDefaultSystems(activity);

        }


        //Initialize the recycler view for the MainSystem list.
        initRecyclerView(activity);

        //Initialize the page's buttons.
        initButtons(activity, schedule);

        //Initialize the page
        initTheme(activity);

        //If this inspection has already been completed in the past
        if(inspectionSchedule.isPastInspection)
        {

            //Every time the Main page is accessed, this code will run. So the schedule
            if(!schedule.inspection.hasLoaded) {
                schedule.inspection.hasLoaded = true;
                schedule.inspection.loadPastInspection(activity);
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

    private void initButtons(final Activity activity, final Schedule inspectionSchedule) {

        Button createSystem = activity.findViewById(R.id.main_create_system);
        createSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SystemsDialog(activity, inspectionSchedule.inspection.getSystemList(), null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        systemList.getAdapter().notifyDataSetChanged();

                        //Save the configuration of the inspection when a system is created
                        Configuration.saveInspectionConfiguration(activity);
                    }
                });
            }
        });

        ProgressBar progressBar = activity.findViewById(R.id.main_inspection_progress);
        TextView progressText = activity.findViewById(R.id.inspection_progress);

        //A system is considered complete if it is excluded or marked as complete.
        int systemsComplete = 0;

        //Get all the systems, custom or made by user.
        ArrayList<System> allSystems = new ArrayList<>();
            allSystems.addAll(inspectionSchedule.inspection.getSystemList());
            allSystems.addAll(inspectionSchedule.inspection.getCustomSystems());
        for (System system : allSystems)
            if (system.isComplete() || system.isExcluded())
                systemsComplete++;

        if(inspectionSchedule.inspection.getSystemList().size() > 0)
        {

            int progress = (int) (100 * ((double) systemsComplete / (double) inspectionSchedule.inspection.getSystemList().size()));
            String textProgress = "Progress (" + progress + "%):";
            progressText.setText(textProgress);

            progressBar.setProgress(progress);
        }
        else
            progressBar.setProgress(0);


        TextView pdf = activity.findViewById(R.id.inspection_main_pdf);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Before the PDF can be accessed, check if all the Major Components of the Inspection are fulfilled.
                ArrayList<String> unsatisfiedComponents = new ArrayList<>();
                for(MajorComponent component : inspectionSchedule.inspection.getMajorComponents())
                {

                    if (!component.getCompletionStatus())
                        unsatisfiedComponents.add(component.getComponentDescription());

                }

                //Check if there were any incomplete MajorComponents.
                if (!unsatisfiedComponents.isEmpty())
                {

                    Log.d("UPDATE", "Unable to open PDF: " + unsatisfiedComponents);

                    //Build a list showing the incomplete MajorComponents.
                    StringBuilder errorMessage = new StringBuilder("Incomplete Components!\n");
                    for (int i = 0; i < unsatisfiedComponents.size(); i++)
                    {

                        String desc = unsatisfiedComponents.get(i);
                        errorMessage.append(desc);
                        if  (i+1<unsatisfiedComponents.size())
                            errorMessage.append(", ");

                    }

                    new ErrorAlert(v.getContext(), errorMessage.toString()); // Display an Error Dialog that shows what MajorComponents aren't completed

                }
                else // There are no incomplete MajorComponents
                {

                    //Check if all the Systems are COMPLETE, or EXCLUDED
                    if (progressBar.getProgress() == 100)
                    {

                        //Render the PDF preview.
                        Intent pdfPreview = new Intent(activity, PDFActivity.class);
                        activity.startActivity(pdfPreview);

                    }
                    else
                    {

                        new ErrorAlert(v.getContext(), "Not all systems are complete/excluded!");
                        Log.d("UPDATE", "Unable to open PDF");
                    }

                }


            }
        });

        TextView client = activity.findViewById(R.id.inspection_main_address);
        client.setText(inspectionSchedule.address);
        //Client information is NOT saved in an inspection file. This is for privacy reasons,
        // but a future feature might allow for frequent clients to save their information with us.
        if (!inspectionSchedule.isPastInspection)
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

    private void initRecyclerView(Activity mActivity)
    {

        systemList = mActivity.findViewById(R.id.inspection_main_systems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        TextView emptyView = mActivity.findViewById(R.id.inspection_main_systems_emptyView);
        ArrayList<System> systems = new ArrayList<>();
        systems.addAll(inspectionSchedule.inspection.getCustomSystems());
        systems.addAll(inspectionSchedule.inspection.getSystemList());

        InspectionSystemAdapter systemListAdapter = new InspectionSystemAdapter(systemList, emptyView, systems, false );

        systemList.setAdapter(systemListAdapter);
        systemList.setLayoutManager(linearLayoutManager);

    }

}
