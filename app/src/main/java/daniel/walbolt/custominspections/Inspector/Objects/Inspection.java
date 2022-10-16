package daniel.walbolt.custominspections.Inspector.Objects;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.ProgressDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MajorComponent;
import daniel.walbolt.custominspections.Inspector.Pages.Front;
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
    private ArrayList<System> customSystems; // Stores all the systems that are hard coded in every inspection. These systems are separated so the PDF doesn't try and render unique systems that don't have the same architecture.
    private ArrayList<System> allSystems; // Stores both the user-created systems and custom systems. This data is used for the RecyclerView in the Main page.
    private ArrayList<MajorComponent> majorComponents; // Stores all items that are required in each inspection.

    private Comparator<System> systemSorter = new Comparator<System>() {

        @Override
        public int compare(System sys1, System sys2) {
            return sys2.getDisplayName().compareTo(sys1.getDisplayName());
        }
    };

    public Inspection(Activity mActivity)
    {

        //Delete any possible pictures that are saved on the device from a previous inspection
        InspectionMedia.deleteDirectories(mActivity);

        systemList = new ArrayList<>();
        customSystems = new ArrayList<>();
        allSystems = new ArrayList<>();
        majorComponents = new ArrayList<>();

    }

    // Load the systems that are stored in System Preferences.
    public void loadDefaultSystems(Activity mActivity)
    {

        /*
        Add all the custom systems here. As of right now, the Front System is the only hard coded system.
         */
        Front frontSystem = new Front();
        addCustomSystem(frontSystem);

        Configuration.loadInspectionConfiguration(mActivity);

    }

    //This method is called by the SystemActivity class when finding the system that it is intended to display
    public System getSystemByName(String systemName)
    {

        for(System system : getAllSystems())
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
        for(System system : systemList) // Save every system created by the user
        {

            InspectionData systemData = system.save();
            if(systemData != null)
                data.add(systemData);

        }

        for (System system : customSystems) // Save every system hard-coded.
        {

            InspectionData systemData = system.save();
            if (systemData != null)
                data.add(systemData);

        }

        return data;

    }

    //Returns the system list, this method should only be used for retrieving just the user-created systems.
    public ArrayList<System> getSystemList()
    {

        return systemList;

    }

    //Returns the custom system list, this method should only be used for retrieving the custom systems.
    public ArrayList<System> getCustomSystems()
    {

        return customSystems;

    }

    //Returns both the user-created, and custom systems list. This method should be used for the Main page.
    public ArrayList<System> getAllSystems()
    {

        return allSystems;

    }

    public void addUserSystem(System newSystem)
    {

        systemList.add(newSystem); // Add the user system to the user-created system list.
        allSystems.add(newSystem); // Add the system to the combined system list as well.

    }

    public void removeUserSystem(System system)
    {

        systemList.remove(system);
        allSystems.remove(system);

    }

    public void addCustomSystem(System customSystem)
    {

        customSystems.add(customSystem);
        allSystems.add(customSystem);


    }

    public void loadPastInspection(Context context) // Starts the loading process
    {

        ProgressDialog loading = new ProgressDialog(context); // Open a dialog that will show the loading progress to the user.
        new FirebaseBusiness().fetchInspectionSystems(loading);

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

        for (System system : customSystems) // Loop through all the custom systems, these are systems that are hard coded
        {

            //Find data that belongs to the custom systems
            if (allSystemsData.containsKey(system.getDisplayName()))
            {

                java.lang.System.out.println("Found custom system data : " + system.getDisplayName());
                //Get the system's data out of all the systems' data.
                Map<String, Object> systemData = (Map<String, Object>)allSystemsData.getOrDefault(system.getDisplayName(), new HashMap<String, Object>());

                system.loadFrom(context, systemData);

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

        for (System inspectedSystem : getCustomSystems())
        {

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
