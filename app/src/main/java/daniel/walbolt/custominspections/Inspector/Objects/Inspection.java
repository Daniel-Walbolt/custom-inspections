package daniel.walbolt.custominspections.Inspector.Objects;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.ProgressDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.UploadDialog;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;
import daniel.walbolt.custominspections.R;

public class Inspection
{


    /*

    Singleton class

    Represents the final report

    Controls the progress bar

    stores all the information of every inspection.

     */

    public boolean hasLoaded = false;

    private ArrayList<System> systemList;               // Stores all the system that currently are in the report.

    public Inspection()
    {

        systemList = new ArrayList<>();

    }


    public void loadDefaultSystems(Activity mActivity)
    {

        //Every time an inspection is loaded, it loads the saved list of systems from the database NOT the saved systems in the inspection.
        // If a past inspection is loaded, inconsistencies will be discarded.

        //TODO: load systems from the saved system configuration in the database

    }

    /*//Upload the information of this inspection into the database
    public void upload(UploadDialog dialog)
    {

        FirebaseBusiness database = FirebaseBusiness.getInstance();
        ArrayList<InspectionData> inspectionData = getInspectionData();

        database.startUpload(dialog, inspectionData);

    }

    //Convert this inspection's information into an object more easy to save to the database.
    private ArrayList<InspectionData> getInspectionData()
    {

        ArrayList<InspectionData> data = new ArrayList<>();
        for(MainSystem system : systemList)
        {

            InspectionData systemData = system.save();
            if(systemData != null)
                data.add(systemData);

        }

        return data;

    }
*/
    public ArrayList<System> getSystemList()
    {

        return systemList;

    }

    public void loadPastInspection(Context context, Main systemPage) // Starts the loading process
    {

        ProgressDialog loading = new ProgressDialog(context);
        FirebaseBusiness.getInstance().fetchInspectionSystems(context, loading, systemPage);

    }

    public void finalizeLoadedInspection(Context context, Map<String, Object> allSystemsData, Main systemPage) // Method called when loading has fetched the files from database
    {

        for(System system : getSystemList())
        {

            if(allSystemsData.containsKey(system.getDisplayName()))
            {

                Map<String, Object> systemData = (Map<String, Object>)allSystemsData.get(system.getDisplayName());

                if(systemData == null) continue; // If the data happens to not exist for this system, ignore it.

                if(systemData.containsKey("System"))
                {

                    //system.loadFrom((Map<String, Object>)systemData.get("System"), context);

                }

                if(system.getSubSystems() != null)
                {

                    Map<String, Object> subSystemsList = (Map<String, Object>)systemData.get("SubSystems"); // Load from the database list because it is possible not every sub-system is saved.

                    if(subSystemsList != null)
                    {

                        ArrayList<String> subSystemConstants = new ArrayList<>();
                        for(System subSystem : system.getSubSystems())
                            subSystemConstants.add(subSystem.getDisplayName());

                        for(String savedSubSystemConstant : subSystemsList.keySet())
                        {

                            System subSystem = system.getSubSystemByStringConstant(savedSubSystemConstant);

                            if(subSystem != null)
                            {

                                //subSystem.loadFrom((Map<String, Object>)subSystemsList.get(savedSubSystemConstant), context);

                            }

                        }

                    }

                }

            }

        }

        systemPage.updateSystemList();

    }

    public ArrayList<String> getInspectedSystems()
    {

        ArrayList<String> inspectedSystems = new ArrayList<>();

        for(System inspectedSystem : getSystemList())
        {

            inspectedSystems.add(inspectedSystem.getDisplayName());

        }

        return inspectedSystems;

    }

}
