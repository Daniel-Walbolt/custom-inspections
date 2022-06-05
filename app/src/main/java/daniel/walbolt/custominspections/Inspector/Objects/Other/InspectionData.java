package daniel.walbolt.custominspections.Inspector.Objects.Other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Objects.System;

public class InspectionData
{

    //The system that this data belongs to
    private System system;

    //Data that is stored in the immediate document
    private Map<String, Object> systemData;

    //Available list to add media objects
    //This list will mostly be used by Category data
    private ArrayList<InspectionMedia> pictures;

    //This data list stores the data for every subsystem in the MainSystem
    private ArrayList<InspectionData> subSystemData;

    //This data list stores the data for every category in the MainSystme
    private ArrayList<Map<String, Object>> categoryData;

    //Inspection Data is always created by a system.
    public InspectionData(System system)
    {

        this.system = system;

        systemData = new HashMap<>();
        subSystemData = new ArrayList<>();
        categoryData = new ArrayList<>();

    }


    public void addSystemData(Map<String, Object> systemData)
    {

        this.systemData.putAll(systemData);

    }

    //This method is used by database.
    public boolean hasPictures()
    {

        return pictures != null && !pictures.isEmpty();

    }

    //This method is used by database
    public ArrayList<InspectionMedia> getPictures()
    {

        if(pictures == null )
            pictures = new ArrayList<>();
        return pictures;

    }

    public void addPicture(InspectionMedia media)
    {

        getPictures().add(media);

    }

    public void addSubSystemData(InspectionData subSystem)
    {

        if(subSystem != null)
        {

            getSubSystemData().add(subSystem);

        }

    }

    public void addCategoryData(Map<String, Object> data)
    {

        if(data != null)
        {

            categoryData.add(data);

        }

    }

    public String getSystemName()
    {

        return system.getDisplayName();

    }

    public boolean hasSubSystems()
    {

        return !(subSystemData.isEmpty());

    }

    public ArrayList<InspectionData> getSubSystemData()
    {

        if(subSystemData == null)
            subSystemData = new ArrayList<>();

        return subSystemData;

    }

    public Map<String, Object> getSystemData()
    {

       return systemData;

    }

    public ArrayList<Map<String, Object>> getCategoryData()
    {

        return categoryData;

    }

    //Return the amount of HashMaps this class has
    // This int is mainly used for animating the upload progress to the database
    public int getDataPoints()
    {

        int dataPoints = 1; //Itself
        for(InspectionData subSystems : getSubSystemData()) // Add the data points of all the nested data.
        {

            dataPoints += subSystems.getDataPoints();

        }

        //Every category is saved by a nested HashMap.
        dataPoints += categoryData.size();

        return dataPoints;

    }

    enum DataType {

        SYSTEM,
        CATEGORY,
        SUBSYSTEM;

        public String getType() {
            return this.toString();
        }

    }

}
