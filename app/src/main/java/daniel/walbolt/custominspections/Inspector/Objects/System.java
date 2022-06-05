package daniel.walbolt.custominspections.Inspector.Objects;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.xw.repo.BubbleSeekBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Activities.SystemActivity;
import daniel.walbolt.custominspections.Constants.SystemSetting;
import daniel.walbolt.custominspections.Constants.SystemTags;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Defect;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Information;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Media;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Observations;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Restrictions;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Settings;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Sub_System;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.SettingItem;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.MainActivity;
import daniel.walbolt.custominspections.PDF.Module;
import daniel.walbolt.custominspections.R;

public class System implements Serializable
{

    /*

    The System object is the controller of an entire System page/activity.
    These pages collectively gather and store all the information of an inspection.

    They are modular, and very customizable. Automatically assembled in a blank layout at runtime.

     */


    //Context Media
    private Media systemMedia;

    private String displayName;
    private System parentSystem;

    protected ArrayList<Category> categories;
    protected ArrayList<System> subSystems;

    private boolean isQuality = false;
    private boolean isComplete = false;
    private boolean isPartial = false;
    private boolean isExcluded = false;

    public System(String displayName, System parent)
    {

        this.displayName = displayName;
        this.parentSystem = parent; // This may be null if the parent is a Main System.

        categories = new ArrayList<>(); // Initialize category list
        subSystems = new ArrayList<>(); // Initialize sub system list

        createDefaultCategories();

    }

    protected void createDefaultCategories()
    {

        // If this system is brand new, every category should appear.
        // If this system was loaded from Configuration, categories should already exist in the system

        //If there are no categories, create them
        if(categories.isEmpty())
        {

            categories.add(new Media(this));
            categories.add(new Information(this));
            categories.add(new Observations(this));
            categories.add(new Restrictions(this));
            categories.add(new Defect(this));
            if(!isSubSystem())
                categories.add(new Sub_System(this));
            categories.add(new Settings(this));

        }

    }

    //Method to organize all the categories currently in the system.
    // When loaded from configuration, the categories aren't in the desired order.
    private void organizeCategories()
    {

        //Rearrange the categories to the correct order
        Category[] organizedCategories = new Category[7];

        for(Category category : categories)
        {

            if(category instanceof Media)
                organizedCategories[0] = category;
            if(category instanceof Information)
                organizedCategories[1] = category;
            else if(category instanceof Observations)
                organizedCategories[2] = category;
            else if(category instanceof Restrictions)
                organizedCategories[3] = category;
            else if(category instanceof Defect)
                organizedCategories[4] = category;
            else if(category instanceof Sub_System)
                organizedCategories[5] = category;
            else if(category instanceof Settings)
                organizedCategories[6] = category;

        }

        categories.clear();

        for(Category category : organizedCategories)
        {

            if(category != null)
                categories.add(category);

        }

    }

    //Reload a category currently displayed on the screen.
    // Every category has an internal recycler that can be updated.
    public void reloadCategory(Category.TYPE type)
    {

        for(Category category : categories)
        {

            if(category.getType() == type)
                category.updateRecycler();

        }

    }

    //Change a setting of this system object
    public void setSetting(SystemSetting setting, boolean status)
    {

        if(setting == SystemSetting.COMPLETE)
            isComplete = status;
        if(setting == SystemSetting.PARTIAL)
            isPartial = status;
        if(setting == SystemSetting.EXCLUDED)
            isExcluded = status;
        if(setting == SystemSetting.QUALITY)
            isQuality = status;

    }

    //Get the statuses of this system based off its settings.
    public ArrayList<SystemTags> getStatus()
    {

        ArrayList<SystemTags> systemTags = new ArrayList<>();

        if(isExcluded)
        {

            systemTags.add(SystemTags.EXCLUDED);
            return systemTags;

        }

        if(isComplete)
            systemTags.add(SystemTags.COMPLETE);
        else if(isPartial)
            systemTags.add(SystemTags.PARTIAL);
        else
            systemTags.add(SystemTags.INCOMPLETE);

        if(isQuality)
            systemTags.add(SystemTags.QUALITY);

        return systemTags;

    }

    //This method is mainly used to check if this system is a sub-system.
    public System getParentSystem()
    {

        return parentSystem;

    }

    public boolean isSubSystem()
    {

        //A system is a sub system if it has a parent system.
        return (parentSystem != null);

    }

    /*

    Getter methods

    */
    public boolean isQuality()
    {

        return isQuality;

    }

    public boolean isExcluded()
    {

        return isExcluded;

    }

    public boolean isComplete()
    {

        return isComplete;

    }

    public boolean isPartial()
    {

        return isPartial;

    }

    public String getDisplayName() { return displayName; }

    public ArrayList<Category> getCategories()
    {

        return categories;

    }

    public Category getCategory(Category.TYPE type)
    {

        for(Category category : categories)
        {

            if (category.getType() == type)
                return category;

        }

        return null;

    }

    public int getMediaCount()
    {

        int mediaCount = 0;

        for(Category category : categories)
            mediaCount += category.getMediaCount();

        return mediaCount;

    }

    public int getCommentCount()
    {

        int commentCount = 0;

        for(Category category : categories)
            commentCount += category.getCommentCount();

        return commentCount;

    }

    public ArrayList<System> getSubSystems()
    {

        return subSystems;

    }

    public System getSubSystemByName(String subSystemName)
    {

        if(subSystems != null)
        {

            for(System subSystem : subSystems)
            {

                if(subSystem != null)
                    if(subSystem.getDisplayName().equals(subSystemName))
                    {

                        return subSystem;

                    }

            }

        }
        return null; // The subsystem was not found in this system.

    }

    /*
    Mutator methods
     */

    //Method that is ONLY called by the CONFIGURATOR when loading
    public void addCategory(Category category)
    {

        categories.add(category);

    }

    //Method that is ONLY called by the CONFIGURATOR when loading
    public void addSubSystem(System subSystem)
    {

        subSystems.add(subSystem);

    }


    //public ArrayList<Module> getPDFModules();

    public void open(Activity mActivity) //the method that redirects activity to the system's page.
    {

        mActivity.setContentView(R.layout.system_page);

        //Add animation to layout changes
        LinearLayout page = mActivity.findViewById(R.id.system_page_container);
        page.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        TextView title = mActivity.findViewById(R.id.system_title);
        title.setText(getDisplayName());

        organizeCategories();

        //Initialize the page
        initTheme(mActivity);

        Button deleteSystem = mActivity.findViewById(R.id.system_page_delete);
        deleteSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConfirmAlert(view.getContext(), "Deleting this system is irreversible.",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delete((SystemActivity) mActivity);
                            }
                        });
            }
        });

        //Load the categories on to the page
        for(Category category : categories) {
            if(category != null)
                category.loadToPage(page);
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

    // Save this system by turning it into a data object that the Database implementation can handle
    public InspectionData save()
    {

        //Create a Data object
        InspectionData systemInformation = new InspectionData(this);

        //Create a list to store THIS system's data.
        Map<String, Object> data = new HashMap<>();

        //Data in the system document will only be the system's settings
        //All other data will be stored in the Category collection of the system document.
        //Every system will also have a subsystem collection.

        //Loop through every category
        for(Category category : categories)
        {

            //The Settings category defines aspects of the system itself, and therefore should be saved in the system
            if (category instanceof Settings)
                data.put("SETTINGS", category.save(systemInformation)); // This is the only data saved to the system's file
            else if(category instanceof Sub_System)
            {

                for(System subSystem : getSubSystems())
                    systemInformation.addSubSystemData(subSystem.save());

            }
            else {
                //Create data for the category
                systemInformation.addCategoryData(category.save(systemInformation));
            }

        }



        systemInformation.addSystemData(data);

        return systemInformation;

    }

    //Load this system's data
    public void loadFrom(Context context, Map<String, Object> allSystemData)
    {

        //allSystemData contains the  data of this system, its categories, its subsystems, and its subsystems data and categories.


        //Separate all of the different types of data
        if (allSystemData.containsKey("System"))
        {

            Map<String, Object> systemData = (Map<String, Object>) allSystemData.get("System");
            
            /*
            Load system settings
             */
            if (systemData.containsKey("SETTINGS"))
                getCategory(Category.TYPE.SETTINGS).loadFrom(context, (Map<String,Object>) systemData.get("SETTINGS"));

        }

        if (allSystemData.containsKey("Data"))
        {

            //Get the data HashMap from the system data
            Map<String, Object> subData = (Map<String, Object>) allSystemData.get("Data");

            if (subData.containsKey("Categories"))
            {

                //Get the Category data HashMap from the subData
                Map<String, Map<String, Object>> allCategoryData = (Map<String, Map<String, Object>>) subData.get("Categories");

                //Loop through all the data sets
                for (Map<String, Object> categoryData : allCategoryData.values())
                {

                    //Get the category info
                    Map<String, Object> categoryInfo = (Map<String,Object>) ((HashMap<String, Object>) categoryData).get("Category Info");
                    String categoryType = (String) categoryInfo.get("Type"); // Get the category type

                    //Load each category's information
                    getCategory(Category.TYPE.valueOf(categoryType)).loadFrom(context, (Map<String, Object>) categoryData);


                }

            }

            if (subData.containsKey("Sub Systems"))
            {

                //Get the data for all subsystems in this system
                Map<String, Object> allSubSystemData = (Map<String, Object>) subData.get("Sub Systems");

                //Loop through the sub systems
                for (System subSystem : getSubSystems())
                {

                    if (allSubSystemData.containsKey(subSystem.getDisplayName()))
                    {

                        //Get the data of the subsystem
                        Map<String, Object> subSystemData = (Map<String, Object>) allSubSystemData.get(subSystem.getDisplayName());

                        subSystem.loadFrom(context, subSystemData);

                    }

                }

            }

        }

    }

    //Method that deletes this system from the inspection
    public void delete(SystemActivity activity)
    {

        //Remove the system from the inspection
        Main.inspectionSchedule.inspection.getSystemList().remove(this);

        activity.onBackPressed();

        //Delete all the saved configurations of the items inside this system.
        //While the system will no longer be saved, the items that belong to it will still persist in the preferences if we don't clear them.
        Configuration.deleteSystemConfiguration(activity, this);

    }

    public int countCommentsIn(ArrayList<? extends CategoryItem>... items)
    {

        int comments = 0;

        for(ArrayList<? extends CategoryItem> itemList : items)
            for(CategoryItem item : itemList)
            {

                if(item.isApplicable())
                    comments += item.getCommentCount();

            }

        return comments;

    }

    public int countPicturesIn(ArrayList<? extends CategoryItem>... items)
    {

        int media = 0;

        for(ArrayList<? extends CategoryItem> itemList : items)
            for(CategoryItem item : itemList)
            {

                if(item.isApplicable())
                    if(item.hasPictures())
                        media += item.getMediaCount();

            }

        return media;

    }

}
