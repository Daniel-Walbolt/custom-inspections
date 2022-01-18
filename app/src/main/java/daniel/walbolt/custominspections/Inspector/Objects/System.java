package daniel.walbolt.custominspections.Inspector.Objects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.xw.repo.BubbleSeekBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Activities.SystemActivity;
import daniel.walbolt.custominspections.Constants.SystemTags;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Defect;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Information;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Media;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Observations;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Restrictions;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Settings;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Sub_System;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.MainActivity;
import daniel.walbolt.custominspections.R;

public class System implements Serializable
{

    /*

    The System object holds all the information of an inspection!
    They are modular, and very customizable. Automatically assembled at runtime.

     */

    //Information Category
    private Information information;

    //Observation Category
    private Observations observations;

    //Restrictions Category
    private Restrictions restrictions;

    //Defect Category
    private Defect defects;

    //Subsystem Category
    private Sub_System subSystems;

    //Settings object
    private Settings settings;
    //Context Media
    private Media systemMedia;

    //Temporary variables for taking pictures.
   /*
    RecyclerView currentMediaRecycler;
    private ArrayList<? extends InspectorCheckable> currentCheckableList;
    private ArrayList<InspectionMedia> currentMediaList;
    */

    private String displayName;
    private System parentSystem;

    ArrayList<Category> categories;

    public System(String displayName, System parent)
    {

        this.displayName = displayName;
        this.parentSystem = parent; // This may be null if the parent is a Main System.

        categories = new ArrayList<>(); // Initialize category list

        /*settings.add(new CheckListItem("System is Complete", SystemSettingsConstants.COMPLETE));
        settings.add(new CheckListItem("Partially Complete", SystemSettingsConstants.PARTIAL));
        settings.add(new CheckListItem("Quality of Residence", SystemSettingsConstants.QUALITY).setHasComments(true, false));
*/
    }

    private void createDefaultCategories()
    {

        if(categories.isEmpty())
        {

            categories.clear();
            categories.add(new Information(this));
            categories.add(new Observations(this));
            categories.add(new Restrictions(this));
            categories.add(new Defect(this));
            categories.add(new Sub_System(this));
            categories.add(new Settings(this));

        }

    }

    public ArrayList<SystemTags> getStatus()
    {

        ArrayList<SystemTags> systemTags = new ArrayList<>();



        return systemTags;

    }

    public boolean isSubSystem()
    {

        //A system is a sub system if it has a parent system.
        return (parentSystem != null);

    }

    public String getDisplayName() { return displayName; }

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

        return null;

    }

    //public ArrayList<Module> getPDFModules();

    public void open(Activity mActivity) //the method that redirects activity to the system's page.
    {

        mActivity.setContentView(R.layout.system_page);

        LinearLayout page = mActivity.findViewById(R.id.system_page_container);
        TextView title = mActivity.findViewById(R.id.system_title);
        title.setText(getDisplayName());

        createDefaultCategories();

        //Initialize the page
        initTheme(mActivity);

        //Load the categories on to the page
        for(Category category : categories)
            category.loadToPage(page);

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

    // The remove category method is called by the system editor dialog
    public boolean removeCategory(Category toRemove)
    {

        //TODO: Reload the page, call open() method?

        boolean removed = categories.remove(toRemove);


        return categories.remove(toRemove);
        
    }

    public System getSubSystemByStringConstant(String subSystemConstant)
    {

        ArrayList<System> subSystems = getSubSystems();

        if(subSystems != null)
        {

            for(System subSystem : subSystems)
            {

                if(subSystem != null)
                    if(subSystem.getDisplayName().equals(subSystemConstant))
                    {

                        return subSystem;

                    }

            }

        }
        return null; // The subsystem was not found in this system.

    }

   /* public InspectionData save() // to database
    {

        InspectionData systemInformation = new InspectionData(this);
        Map<String, Object> data = new HashMap<>();

        data.put("COMPLETE", saveSetting(getSetting(SystemSettingsConstants.COMPLETE)));
        data.put("PARTIAL", saveSetting(getSetting(SystemSettingsConstants.PARTIAL)));
        data.put("RESIDENCE_QUALITY", saveSetting(getSetting(SystemSettingsConstants.QUALITY)));

        systemInformation.addSystemData(data);

        return systemInformation;

    }*/

    Map<String, Object> saveSetting(CategoryItem setting)
    {

        Map<String, Object> status = new HashMap<>();
        status.put("STATUS", setting.isApplicable()); // Save the status of the setting

        if(setting.hasComments() && setting.getCommentCount() > 0)
            status.put("COMMENTS", saveCommentMedia(setting.getCommentMedia())); // Save comments if the setting has comments

        return status;

    }

    void loadSetting(String key, Map<String, Object> systemData, CategoryItem setting)
    {

        if(systemData.containsKey(key))
        {

            Map<String, Object> status = (Map<String, Object>) systemData.get(key); // Retrieve the information saved in the above method
            setting.setApplicability((boolean)status.get("STATUS")); // Set the setting to true or false

            if(status.containsKey("COMMENTS"))
                loadCommentsFrom((ArrayList<String>)status.get("COMMENTS"), setting.getCommentMedia()); // Load comments if they were saved.


        }

    }

   /* public void loadFrom(Map<String, Object> systemData, Context context) // Loads the system from saved information in the database. Context is used for creating image files for pictures.
    {

        loadSetting("COMPLETE", systemData, getSetting(SystemSettingsConstants.COMPLETE));
        loadSetting("PARTIAL", systemData, getSetting(SystemSettingsConstants.PARTIAL));
        loadSetting("RESIDENCE_QUALITY", systemData, getSetting(SystemSettingsConstants.QUALITY));

    }*/
    /*
    The System Data is stored as exampled:
    Map<SYSTEM_CONSTANT, Map<String, Object> systemData>
    Map<"ROOF", Map<"System", Map<String, Object> mainSystemData>>>
    Map<"ROOF", Map<"SubSystems", Map<String, Object> allSubSystemData>>
    Map<"ROOF", Map<"SubSystems", Map<"ROOF_COVERING", Map<String, Object> subSystemData>>>

     */

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

   /* public boolean isQuality()
    {

        return getSetting(SystemSettingsConstants.QUALITY).isApplicable();

    }*/

   /* //Method to initialize the inherited Restrictions recycler
    public void initInheritedRestrictionsRecycler(Activity mActivity, MainSystem system)
    {

        RecyclerView recyclerView = mActivity.findViewById(R.id.inspector_system_inherited_restriction_recycler);
        View emptyView = mActivity.findViewById(R.id.inspector_system_inherited_restriction_recycler_emptyView);
        InheritedRestrictionsAdapter adapter = new InheritedRestrictionsAdapter(system, recyclerView, emptyView);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);

    }*/

/*    //Method to initialize the system settings recycler
    public void initSystemSettingsRecycler(Activity mActivity)
    {

        RecyclerView settingList = mActivity.findViewById(R.id.inspector_system_settings);
        CheckBoxRecyclerAdapter adapter = new CheckBoxRecyclerAdapter(settings);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        settingList.setAdapter(adapter);
        settingList.setLayoutManager(manager);
        settingList.setNestedScrollingEnabled(false);

    }*/

    /*
    Recycler Views
     */
/*
    public void initSubSystemRecycler(Activity mActivity, ArrayList<SubSystem> subSystems)
    {

        RecyclerView recyclerView = mActivity.findViewById(R.id.inspection_system_subsystem_recycler);
        SubSystemAdapterRecycler adapter = new SubSystemAdapterRecycler(this, subSystems, mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        recyclerView.addItemDecoration(new SubSystemItemDecoration());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);

    }
*/

   /* public void addStandardImageRecyclerDecoration(RecyclerView recyclerView)
    {

        SystemMediaItemDecorator decorator = new SystemMediaItemDecorator();
        recyclerView.addItemDecoration(decorator);

    }*/

   /* *//*
    Repeated System Variables
     *//*
    public Observation getGeneralComment(SystemSection section)
    {

        return new Observation("General Comment", section).setHasComments(true, false);

    }

    public Observation getFurtherEvaluationObservation(SystemSection section)
    {

        return new Observation("Further Evaluation Recommended", section).setHasComments(true, false);

    }

    public Observation getServiceExpectancyObservation(SystemSection section)
    {

        return new Observation("Near/Past Service Expectancy", section).setHasComments(true, false);

    }*/

    /*
    Save and Load Custom Comment Media (used for situational objects. i.e. Roof Age)
     */

    public ArrayList<String> saveCommentMedia(InspectionMedia media)
    {

        return media.getComments();

    }

    public void loadCommentsFrom(ArrayList<String> savedComments, InspectionMedia target)
    {

        if(savedComments != null)
        {

            target.addComments(savedComments);

        }

    }

    /*
   Save SubSystem Data
    */
   /* public void saveSubSystemDataTo(InspectionData systemData) // Saves this System's subsystem data (database).
    {

        if(getSubSystems() != null)
            for(SubSystem subSystem : getSubSystems())
            {

                if(subSystem != null)
                    systemData.addSubSystemData(subSystem.save());

            }

    }*/

    /*
    Save and Load restrictions (Database)
     */

    /*public Map<String, Object> saveRestriction(ArrayList<Restriction> restrictions, InspectionData savingTo)
    {

        Map<String, Object> dataToSave = new HashMap<>();

        for(Restriction restriction : restrictions)
        {

            if(restriction.isApplicable())
            {

                dataToSave.put(restriction.getSection().toString(), restriction.save(savingTo));

            }

        }

        return dataToSave;

    }*/

   /* public void loadRestrictions(Context context, String key, Map<String, Object> systemData, ArrayList<Restriction> loadingTo) // Load the comments from the database onto the restriction
    {

        if(systemData.containsKey(key))
        {

            Map<String, Object> restrictionsData = (Map<String, Object>)systemData.get(key);

            for(String savedRestrictionSection : restrictionsData.keySet())
            {

                for(Restriction target : loadingTo)
                {

                    if(target.getSection().toString().equals(savedRestrictionSection))
                    {

                        target.loadFrom(context, (Map<String,Object>)restrictionsData.get(savedRestrictionSection));
                        break;

                    }

                }

            }

        }

    }*/

    /*

    Save and Load observations (Database)

     */
    /*public Map<String, Object> saveObservations(ArrayList<Observation> observations, InspectionData savingTo)
    {

        Map<String, Object> dataToSave = new HashMap<>();

        for(Observation observation : observations)
        {

            if(observation.isApplicable())
            {

                dataToSave.put(observation.getSection().toString(), observation.save(savingTo));

            }

        }

        return dataToSave;

    }*/
    /*public void loadObservations(Context context, String key, Map<String, Object> systemData, ArrayList<Observation> loadingTo) // Load the comments and media from the database into this observation
    {

        if(systemData.containsKey(key))
        {

            Map<String, Object> observationsData = (Map<String, Object>)systemData.get(key);

            for(String savedRestrictionSection : observationsData.keySet())
            {

                for(Observation target : loadingTo)
                {

                    if(target.getSection().toString().equals(savedRestrictionSection))
                    {

                        target.loadFrom(context, (Map<String,Object>)observationsData.get(savedRestrictionSection));
                        break;

                    }

                }

            }

        }

    }*/

    /*

    Save and Load CheckList items (Database)

     */

    /*public Map<String, Object> saveCheckListItems(ArrayList<CheckListItem> items, InspectionData savingTo)
    {

        Map<String, Object> dataToSave = new HashMap<>();

        for(CheckListItem item : items)
            if(item.isApplicable())
                dataToSave.put(item.getSection().toString(), item.save(savingTo));

        return dataToSave;

    }

    public void loadCheckListItems(Context context, String key, Map<String, Object> systemData, ArrayList<CheckListItem> targetCheckListItems) // Load the comments and media from the database into this item
    {

        if(systemData.containsKey(key))
        {

            Map<String, Object> checklistItemsData = (Map<String, Object>)systemData.get(key);

            for(String savedCheckListItem : checklistItemsData.keySet())
            {

                for(CheckListItem checkListItem : targetCheckListItems)
                {

                    if(checkListItem.getSection().toString().equals(savedCheckListItem))
                    {

                        checkListItem.loadFrom(context, (Map<String, Object>)checklistItemsData.get(savedCheckListItem));
                        break;

                    }

                }

            }

        }

    }
*/
    /*

    Save and Load media (Database)

     */

    /*public ArrayList<Map<String, Object>> saveImageMedia(ArrayList<InspectionMedia> media, InspectionData savingTo)
    {

        *//*

        All images are saved in an array list of Map<String, Object>

         *//*
        ArrayList<Map<String,Object>> savedMedia = new ArrayList<>();
        for(InspectionMedia aMedia : media)
        {

            savedMedia.add(aMedia.save());
            if(!aMedia.isCommentMedia()) {
                aMedia.CompressImageFile();
                savingTo.addPicture(aMedia);
            }
        }

        return savedMedia;

    }*/

    /*public void loadImageMediaTo(Context context, String key, Map<String, Object> systemData, ArrayList<InspectionMedia> targetList, SystemSection targetSection)
    {

        if(systemData.containsKey(key))
        {

            ArrayList<Map<String, Object>> mediaData = (ArrayList<Map<String, Object>>)systemData.get(key);

            for(Map<String, Object> imageInfo : mediaData)
            {


                InspectionMedia savedMedia = new InspectionMedia(targetSection).createImageFileFromDatabase(context, (String) imageInfo.get("Name"));
                if(imageInfo.containsKey("Comments"))
                    savedMedia.addComments((ArrayList<String>)imageInfo.get("Comments"));

                targetList.add(savedMedia);

            }

        }

    }*/

}
