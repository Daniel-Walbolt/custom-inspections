package daniel.walbolt.custominspections.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.MainActivity;

public class SystemActivity extends MainActivity
{



    //Variables used to take a picture. Once the picture data is returned, it must be saved with a name specified before calling the camera.
    private String recentPhotoName;

    //The currently-open system is determined by the "onCreate" method inside of a "SystemActivity" class or subclass.
    // By default, the system being created is made the currentlyOpenSystem. Because the super class is called first, when a SubSystemActivity is created there is a "SubSystem" string extra in the Intent.
    // If this intent extra is not null, there is a subsystem being created. Thus, the parent system is defined as the class the subsystem is loaded from, and when the subsystem Activity closes, the parent system is reopened.
    // Reopening is crucial to reloading the contents of the parent system. While it would work to only close the Activity (there is an activity for each page) it would not reload SubSystem Data (Comments/Media), or global restrictions.
    public System currentlyOpenSystem;
    private System parentSystem;

    private CategoryItem currentSection;
    private boolean isMediaList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        boolean isSubSystem = intent.getBooleanExtra("isSubSystem", false);
        String systemName = intent.getStringExtra("SystemName");

        //The system that will be opened is contained in the Intent
        System system;

        //If we're expecting the system to be a Sub System,
        if(isSubSystem)
        {

            //Find the name of the parent system
            String parentSystem = intent.getStringExtra("ParentSystem");

            //Get the parent system from the main system list and find its sub system with the name that matches what was passed in the Intent
            system = Main.inspectionSchedule.inspection.getSystemByName(parentSystem)
                .getSubSystemByName(systemName);

        }
        else
        {

            //The system we are opening has the system name provided in the intent
            //Find the main-system in the main-system list
            system = Main.inspectionSchedule.inspection.getSystemByName(systemName);// Get the system from the intent


        }

        if(system != null)
        {

            //Open the system and define the currentlyOpenSystem as the system being opened.
            currentlyOpenSystem = system;
            system.open(this);

        }
        else
            finish();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        currentlyOpenSystem.open(this);

    }

    public void setRecentPhotoName(String name)
    {

        this.recentPhotoName = name;

    }

    public void setIsMediaList(boolean isMediaList)
    {

        this.isMediaList = isMediaList;

    }

    public void setCurrentSection(CategoryItem section)
    {

        currentSection = section;

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
