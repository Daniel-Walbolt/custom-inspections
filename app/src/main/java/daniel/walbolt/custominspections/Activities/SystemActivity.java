package daniel.walbolt.custominspections.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.Inspection;
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
        String systemName = intent.getStringExtra("SystemName");

        if(!systemName.isEmpty())
        {

            java.lang.System.out.println(systemName);
            System system = Main.inspectionSchedule.inspection.getSystemByName(intent.getStringExtra("SystemName"));// Get the system from the intent
            if(system != null)
            {

                currentlyOpenSystem = system;
                system.open(this);

            }

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(!currentlyOpenSystem.isSubSystem())
        {

            currentlyOpenSystem.open(this);

        }

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
