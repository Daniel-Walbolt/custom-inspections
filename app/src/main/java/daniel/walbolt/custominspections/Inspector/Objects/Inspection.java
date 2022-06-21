package daniel.walbolt.custominspections.Inspector.Objects;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.ProgressDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MajorComponent;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;

public class Inspection
{


    /*

    Singleton class

    Connects systems, database, and customers together

    Controls the progress bar of the inspection

    stores all the information of every inspection.

     */

    public boolean hasLoaded = false;

    private ArrayList<System> systemList; // Stores all the system that currently are in the report.
    private ArrayList<MajorComponent> majorComponents; // Stores all items that are required in each inspection.

    public Inspection(Activity mActivity)
    {

        //Delete any possible pictures that are saved on the device from a previous inspection
        InspectionMedia.deleteDirectories(mActivity);

        systemList = new ArrayList<>();
        majorComponents = new ArrayList<>();

    }

    // Load the systems that are stored in System Preferences.
    public void loadDefaultSystems(Activity mActivity)
    {

        Configuration.loadInspectionConfiguration(mActivity);

    }

    //This method is called by the SystemActivity class when finding the system that it is intended to display
    public System getSystemByName(String systemName)
    {

        for(System system : systemList)
        {

            if(system.getDisplayName().equals(systemName))
            {

                return system;

            }

        }

        return null;

    }

    //Upload the information of this inspection into the database
    public void upload(ProgressDialog dialog)
    {

        FirebaseBusiness database = new FirebaseBusiness();
        ArrayList<InspectionData> inspectionData = getInspectionData();

        database.startUpload(dialog, inspectionData);

    }

    //Convert this inspection's information into a list of objects. This makes it easier to save to the database.
    private ArrayList<InspectionData> getInspectionData()
    {

        ArrayList<InspectionData> data = new ArrayList<>();
        for(System system : systemList)
        {

            InspectionData systemData = system.save();
            if(systemData != null)
                data.add(systemData);

        }

        return data;

    }

    public ArrayList<System> getSystemList()
    {

        return systemList;

    }

    public void loadPastInspection(Context context) // Starts the loading process
    {

        ProgressDialog loading = new ProgressDialog(context);
        new FirebaseBusiness().fetchInspectionSystems( loading);

    }

    public void finalizeLoadedInspection(Context context, Map<String, Object> allSystemsData) // Method called when loading has fetched the system information from the database
    {

        //Loop through every system that was loaded by Configuration
        for(System system : getSystemList())
        {

            //Find the data for every system possible.
            if(allSystemsData.containsKey(system.getDisplayName()))
            {

                java.lang.System.out.println("Found system data for :  " + system.getDisplayName());
                Map<String, Object> allSystemData = (Map<String, Object>)allSystemsData.getOrDefault(system.getDisplayName(), new HashMap<String, Object>());

                system.loadFrom(context, allSystemData);

            }

        }
    }

    public ArrayList<String> getInspectedSystems()
    {

        ArrayList<String> inspectedSystems = new ArrayList<>();

        for(System inspectedSystem : getSystemList())
        {

            //Here we store the name of the exact name of the system.
            // This list is used for the database to know the system files to look for while loading.
            inspectedSystems.add(inspectedSystem.getDisplayName());

        }

        return inspectedSystems;

    }

    public void addMajorComponent(MajorComponent component)
    {

        this.majorComponents.add(component);

    }

    public ArrayList<MajorComponent> getMajorComponents()
    {

        return this.majorComponents;

    }

}
