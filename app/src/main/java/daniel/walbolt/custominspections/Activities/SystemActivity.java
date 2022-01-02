package daniel.walbolt.custominspections.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Objects.SystemSection;
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

    private SystemSection currentSection;
    private boolean isMediaList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        System system = (System) intent.getSerializableExtra("System"); // Get the system from the intent

        currentlyOpenSystem = system;
        system.open(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(!currentlyOpenSystem.isSubSystem())
        {

            currentlyOpenSystem.open(this);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
            if(currentSection != null)
                if(isMediaList)
                    currentSection.getSystem().addMediaListImage(this, currentSection, recentPhotoName);
                else
                    currentSection.getSystem().addCheckableImage(this, currentSection, recentPhotoName);

    }

    public void setRecentPhotoName(String name)
    {

        this.recentPhotoName = name;

    }

    public void setIsMediaList(boolean isMediaList)
    {

        this.isMediaList = isMediaList;

    }

    public void setCurrentSection(SystemSection section)
    {

        currentSection = section;

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
