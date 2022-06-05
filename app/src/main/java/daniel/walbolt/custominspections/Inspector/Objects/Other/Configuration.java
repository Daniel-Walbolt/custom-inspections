package daniel.walbolt.custominspections.Inspector.Objects.Other;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Defect;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Information;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Media;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Observations;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Restrictions;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Settings;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Sub_System;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Checkbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.DefectItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.ObservationItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.RestrictionItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Slider;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Pages.Main;

public class Configuration
{

    /*

    The configuration class saved and loads the configuration of the custom inspection checklist

    Configurations are used for every Category. This is to maintain clutter of this class, (managing 1 category instead of every category in a system).


     */

    //The location where the list of Main-System names is located in.
    private static final String inspectionConfigurationLocation = "CONFIGURATION";
    private static final String mainSystemLocation = "MAIN_SYSTEMS"; // The preference key in the inspectionConfiguration where the Main-system list is stored

    //The location in every System's configuration where Category names are stored.
    private static final String systemCategoryLocation = "CATEGORIES";

    //The preference key where every item in a category has its name stored
    private static final String categoryItemsLocation = "ITEMS";

    //The preference key where the list of item's in a Category Group are stored
    private static final String groupItemsLocation = "GROUPITEMS";

    //THe preference key where the list of subsystem names are stored in a SubSystem Category
    private static final String subSystemCategoryLocation = "SUBSYSTEMS";

    private static final String typeLocation = "TYPE";
    private static final String descLocation = "DESC";
    private static final String pdfDescLocation = "PDFDESC";
    private static final String hintLocation = "HINT";
    private static final String sliderTextLocation = "SLIDER_TEXT";
    private static final String IDLocation = "ID";

    //The default value of the 'typeLocation' key
    private static final String defaultType = "CHECKBOX";

    //Save the configuration of the entire inspection
    public static void saveInspectionConfiguration(Context context)
    {

        //Configuration preferences store all the main-system names
        SharedPreferences preferences = context.getSharedPreferences(inspectionConfigurationLocation, Context.MODE_PRIVATE);

        //Preferences are stored in a specific name space
        //SYSTEMS : {all main-system names}
        //From the system names:
        //{main-system name} : CATEGORIES : {all system category names}
        //From the categories list
        ///{main-system name}_{category name} : ITEMS : {category item names}
        //From the CategoryItem name list:
        //{main-system name}_{category name}_{category item name} will hold several strings:
        // TYPE: {category item type}
        // HINT: {category item comment hint} "" if null
        // PDFDESC: {category item pdf description} "" if null
        // DESC: {category item description} "" if null

        //Start from the beginning by getting all the created systems in the inspection
        ArrayList<System> systemList = Main.inspectionSchedule.inspection.getSystemList();

        //Create a list from the system list using the system names
        Set<String> systems = new HashSet<>();
        for(System system : systemList)
        {

            systems.add(system.getDisplayName());
            saveSystemConfiguration(context, system);

        }

        //Save the names of all the systems
        preferences.edit().putStringSet(mainSystemLocation, systems).apply();

    }

    //This method will load the entire inspection
    public static void loadInspectionConfiguration(Context context)
    {

        SharedPreferences preferences = context.getSharedPreferences(inspectionConfigurationLocation, Context.MODE_PRIVATE);

        //Get all the expected systems by getting the list of system names
        Set<String> systemNames = preferences.getStringSet(mainSystemLocation, new HashSet<>());

        //Loop through all the found system names
        for(String systemName : systemNames)
        {

            //Create the main system
            System mainSystem = loadMainSystemConfiguration(context, systemName);

            //Add the loaded system onto the inspection
            Main.inspectionSchedule.inspection.getSystemList().add(mainSystem);

        }

    }

    //The system is the most abstract unit of an inspection
    //And there is the minimum requirement for saving
    //When making changes, configuration is deleted and re-saved on a system level.
    public static void saveSystemConfiguration(Context context, System system)
    {

        //Get the preference location of the system
        SharedPreferences systemPreferences = getSystemPreferences(context, system.getDisplayName(), system.isSubSystem() ? system.getParentSystem().getDisplayName() : null,
                system.isSubSystem());

        //Category names need to be stored so the configurator knows where to look when loading
        Set<String> categoryNames = new HashSet<>();

        //Loop through every category of the system
        for (Category category : system.getCategories())
        {

            //Don't try and save settings
            if(!(category instanceof Settings))
            {

                //Add the category to the system's category-name list.
                categoryNames.add(category.getName());
                saveCategoryConfiguration(context, category);

            }


        }

        //Save the system's category list
        systemPreferences.edit().putStringSet(systemCategoryLocation, categoryNames).apply();

    }

    //This method will return a loaded MAIN system from Shared Preferences
    private static System loadMainSystemConfiguration(Context context, String systemName)
    {

        //Find the main system's preferences
        SharedPreferences systemPreferences = getSystemPreferences(context, systemName, null, false);

        //Get the system's categories from preferences
        Set<String> categoryNames = systemPreferences.getStringSet(systemCategoryLocation, new HashSet<>());

        //Create the system
        System system = new System(systemName, null);

        //Creating a system object automatically creates default categories. Remove these.
        system.getCategories().clear();

        //To give the system its original information we have to loop through its categories
        //Loop through every category in the system
        for(String categoryName : categoryNames)
        {

            java.lang.System.out.println("Loading category : " + categoryName);

            Category category = loadCategoryConfiguration(context, system, categoryName);

            system.addCategory(category);

        }

        //The Configuration class never saves or loads settings (they're constant)
        //Add the settings category manually
        system.addCategory(new Settings(system));

        return system;

    }

    public static void saveSubSystemConfiguration(Context context, System subSystem)
    {

        if(subSystem.isSubSystem())
        {

            //Get the Sub System's preferences
            SharedPreferences subSystemPreferences = getSystemPreferences(context, subSystem.getDisplayName(), subSystem.getParentSystem().getDisplayName(), true);

            //Create a list of the categories in the sub system
            Set<String> subSystemCategories = new HashSet<>();

            //Loop through the categories of the SUBSYSTEM
            for(Category subSystemCategory : subSystem.getCategories())
            {

                //Don't attempt to save settings category
                if(!(subSystemCategory instanceof Settings))
                {

                    subSystemCategories.add(subSystemCategory.getName());
                    saveCategoryConfiguration(context, subSystemCategory);

                }

            }

            //Save the Sub System Categories into the preferences
            subSystemPreferences.edit().putStringSet(subSystemCategoryLocation, subSystemCategories).apply();

        }

    }

    public static System loadSubSystemConfiguration(Context context, System parent, String subSystemName)
    {

        //Get the Sub System's preferences
        SharedPreferences subSystemPreferences = getSystemPreferences(context, subSystemName, parent.getDisplayName(), true);

        //Create the system object
        System subSystem = new System(subSystemName, parent);

        //Creating a system object automatically creates default categories. Remove these.
        subSystem.getCategories().clear();

        //Loop through all the names of the categories in the Sub System preferences
        for(String subSystemCategoryName : subSystemPreferences.getStringSet(subSystemCategoryLocation, new HashSet<>()))
        {

            //Load the Sub System category
            Category category = loadCategoryConfiguration(context, subSystem, subSystemCategoryName);

            //Add the category to the sub system
            subSystem.addCategory(category);

        }

        //The Configuration class never saves or loads settings (they're constant)
        //Add the settings category manually
        subSystem.addCategory(new Settings(subSystem));

        return subSystem;

    }

    //After this method is called, the parent configuration is resaved
    public static void deleteItemConfiguration(Context context, CategoryItem item)
    {

        //Get the item's preferences
        SharedPreferences itemPreferences;

        //To delete an item's configuration, we must determine if its in a group
        if(item.getGroup() == null)
        {

            //If the item is a group
            if(item instanceof CategoryGroup)
            {

                //Loop through every one of its items
                for(CategoryItem groupItem : ((CategoryGroup)item).getItems())
                {

                    //And delete every group item's configuration as well
                    deleteItemConfigurations(context, groupItem);

                }

            }

            //The item in question is NOT in a group, only a category
            itemPreferences = getItemPreferences(context, item.getSystem(), item.getCategory(), item.getName());

            //Clear the item's preferences
            itemPreferences.edit().clear().apply();

            //The item's configuration is removed, but the reference to it still exists in the group/category.
            //Because of this, the configurator will attempt to load information from the locations just deleted.
            //To prevent this, we need to re-save the category's configuration. This is also why the item needs removed from the category before deleting its configuration.
            //Removing the item from the category can't be done here because it usually requires a U.I. update as well.
            saveCategoryConfiguration(context, item.getCategory());

        }
        else
        {

            //The item in question IS in a group.
            itemPreferences = getGroupItemPreferences(context, item.getSystem(),
                    item.getCategory(), item.getGroup(), item.getName());

            //Clear the item's preferences
            itemPreferences.edit().clear().apply();

            //The item's configuration is removed, but the reference to it still exists in the group
            //Because of this, the configurator will attempt to load information from the locations just deleted.
            //To prevent this, we need to re-save the group's configuration. This is also why the item needs removed from the group before deleting its configuration.
            //Removing the item from the group can't be done here because it usually requires a U.I. update as well.
            saveGroupConfiguration(context, item.getGroup());

        }

    }

    //This method deleted an item's configuration, but doesn't re-save the group/category configuration
    //This method is only called by internal methods that seek to mass delete items. Re-saving the category/group with every item delete is taxing on system resources.
    private static void deleteItemConfigurations(Context context, CategoryItem item)
    {

        //Get the item's preferences
        SharedPreferences itemPreferences;

        //To delete an item's configuration, we must determine if its in a group
        if(item.getGroup() == null)
        {

            //If the item is a group
            if(item instanceof CategoryGroup)
            {

                //Loop through every one of its items
                for(CategoryItem groupItem : ((CategoryGroup)item).getItems())
                {

                    //And delete every group item's configuration as well
                    deleteItemConfigurations(context, groupItem);

                }

            }

            //The item in question is NOT in a group, only a category
            itemPreferences = getItemPreferences(context, item.getSystem(), item.getCategory(), item.getName());

            //Clear the item's preferences
            itemPreferences.edit().clear().apply();

        }
        else
        {

            //The item in question IS in a group.
            itemPreferences = getGroupItemPreferences(context, item.getSystem(),
                    item.getCategory(), item.getGroup(), item.getName());

            //Clear the item's preferences
            itemPreferences.edit().clear().apply();

        }

    }

    //This method should be called after the category has been remove from its system
    public static void deleteCategoryConfiguration(Context context, Category category)
    {

        //Get the category's preferences
        SharedPreferences categoryPreferences = getCategoryPreferences(context, category.getSystem(), category.getName());

        //Get the category's items
        for(CategoryItem categoryItem : category.getCategoryItems())
        {

            //Delete the configuration of the items in the category
            deleteItemConfigurations(context, categoryItem);

        }

        //Instead of saving the entire system, we can instead just save the list of categories in the system's preferences
        SharedPreferences systemPreferences = getSystemPreferences(context, category.getSystem().getDisplayName(),
                category.getSystem().isSubSystem() ? category.getSystem().getParentSystem().getDisplayName() : null, category.getSystem().isSubSystem());

        Set<String> categoryNames = new HashSet<>();

        for(Category systemCategory : category.getSystem().getCategories())
            categoryNames.add(systemCategory.getName());

        systemPreferences.edit().putStringSet(systemCategoryLocation, categoryNames).apply();

    }

    //Method to remove a Main-system or Sub System
    public static void deleteSystemConfiguration(Context context, System system)
    {

        //Get the system's preferences
        SharedPreferences systemPreferences = getSystemPreferences(context, system.getDisplayName(),
                system.isSubSystem() ? system.getParentSystem().getDisplayName() : null, system.isSubSystem());

        for(Category category : system.getCategories())
        {

            //Delete all the category information of the system
            deleteCategoryConfiguration(context, category);

        }

        systemPreferences.edit().clear().apply();

        if (!system.isSubSystem())
        {

            //If the System being deleted is a Main-System, re-save the inspection.
            //Instead of saving the entire inspection configuration, just save its list of systems
            SharedPreferences inspectionPreferences = context.getSharedPreferences(inspectionConfigurationLocation, Context.MODE_PRIVATE);
            Set<String> systemNames = new HashSet<>();
            for(System inspectionSystem : Main.inspectionSchedule.inspection.getSystemList())
            {

                systemNames.add(inspectionSystem.getDisplayName());

            }

            inspectionPreferences.edit().putStringSet(mainSystemLocation, systemNames).apply();

        }
        else
        {

            //The System being deleted is a Sub-System. The list of sub-systems should be re-saved in the parent system.
            //While not exactly eficient. We can just save the entire parent system because the Sub-System category will overwrite the save with the current systems.
            saveSystemConfiguration(context, system.getParentSystem());

        }

    }

    //Save a single category's configuration. This method also works with categories in a Sub System.
    public static void saveCategoryConfiguration(Context context, Category category)
    {

        //Every category's preferences will be stored in relation to their system's name.
        SharedPreferences categoryPreferences = getCategoryPreferences(context, category.getSystem(), category.getName());

        if(category instanceof Sub_System)
        {

            //Create a list to store the names of all the Sub Systems
            Set<String> subSystemNames = new HashSet<>();

            //Loop through the Sub Systems of the parent system.
            for(System subSystem : category.getSystem().getSubSystems())
            {

                //Add the name of every subsystem into the list
                subSystemNames.add(subSystem.getDisplayName());

                saveSubSystemConfiguration(context, subSystem);

            }

            //Save the SubSystem names to the category preferences
            categoryPreferences.edit().putStringSet(subSystemCategoryLocation, subSystemNames).apply();

        }
        else
        {

            //The names of every item in the category needs stored so the configuration knows where to look for loading
            Set<String> categoryItemNames = new HashSet<>();

            //Loop through the category's items
            for(CategoryItem item : category.getCategoryItems())
            {

                //Add the category item's name to the list of item names
                categoryItemNames.add(item.getName());

                //If the CategoryItem is a Group, it might have items in it
                if(item instanceof CategoryGroup)
                    saveGroupConfiguration(context, (CategoryGroup)item);
                else
                    saveItemConfiguration(context, item);

            }

            //Save the category's item list.
            categoryPreferences.edit().putStringSet(categoryItemsLocation, categoryItemNames).apply();

        }

    }

    public static Category loadCategoryConfiguration(Context context, System parentSystem, String categoryName)
    {

        //Get the category's preferences
        SharedPreferences categoryPreferences = getCategoryPreferences(context, parentSystem, categoryName);

        //Create the category based off the name of it (user does not define category name)
        Category category = createCategoryFromString(parentSystem, categoryName);

        //Check if the category is a Sub_System category. If it is, we need to load in all the Sub-Systems too.
        if(category instanceof Sub_System)
        {

            //Get the list of Sub System names
            Set<String> subSystemNames = categoryPreferences.getStringSet(subSystemCategoryLocation, new HashSet<>());

            //Loop through every Sub System name
            for(String subSystemName : subSystemNames)
            {

                //Load the sub system
                System subSystem = loadSubSystemConfiguration(context, parentSystem, subSystemName);

                //Add the sub system to the main system
                category.getSystem().addSubSystem(subSystem);

            }

        }
        else
        {

            //This category is not a sub-system category, and only contains basic items.
            //Get the list of item names that reference their location in the preferences
            Set<String> itemNames = categoryPreferences.getStringSet(categoryItemsLocation, new HashSet<>());

            //Loop through every item
            for(String itemName : itemNames)
            {

                //Load the items that are located within the category
                CategoryItem categoryItem = loadItemConfiguration(context, parentSystem, category, itemName);

                //Add the category item to the category
                category.addItem(categoryItem);

            }

        }

        return category;

    }

    public static void saveGroupConfiguration(Context context, CategoryGroup group)
    {

        SharedPreferences groupPreferences = getItemPreferences(context, group.getSystem(), group.getCategory(), group.getName());

        //Groups don't have hints or descriptions, but they do have a TYPE.
        //Save the group's type into its preferences.
        groupPreferences.edit().putString(typeLocation, getTypeString(group)).apply();

        //Create a list to store the names of the items in the group
        Set<String> groupItemNames = new HashSet<>();

        //Loop through all the items in the group
        for(CategoryItem groupItem : group.getItems())
        {

            groupItemNames.add(groupItem.getName());

            saveGroupItemConfiguration(context, groupItem);

        }

        //Save the names of the items in the group
        groupPreferences.edit().putStringSet(groupItemsLocation, groupItemNames).apply();

    }

    //This method loads the internal items of a CategoryGroup
    public static CategoryGroup loadGroupConfiguration(Context context, CategoryGroup group)
    {

        //The CategoryGroup is always loaded, but the items within the group need loaded.
        //Get the preferences of the group

        SharedPreferences groupPreferences = getItemPreferences(context, group.getSystem(), group.getCategory(), group.getName());

        //Get the list of item names in the group
        Set<String> groupItemNames = groupPreferences.getStringSet(groupItemsLocation, new HashSet<>());

        //Loop through the items in the group
        for(String groupItemName : groupItemNames)
        {

            //Load the item that's in the group
            CategoryItem groupItem = loadGroupItemConfiguration(context, group, groupItemName);

            //Add the item to the group
            group.addItem(groupItem);

        }

        return group;
    }

    //This method loads an item that is located within a group. Group Items have different save locations than normal items.
    public static CategoryItem loadGroupItemConfiguration(Context context, CategoryGroup group, String itemName)
    {

        //Get the preferences of the group item
        SharedPreferences groupItemPreferences = getGroupItemPreferences(context, group.getSystem(), group.getCategory(), group, itemName);

        //Create the item in the group
        CategoryItem groupItem = getTypeFromString(group.getCategory(), itemName,groupItemPreferences.getString(typeLocation, defaultType),
                groupItemPreferences.getLong(IDLocation, 0L));

        loadItemSettings(groupItem, groupItemPreferences);

        return groupItem;

    }

    //This method saves an item in a group. The place to save is slightly different than a normal item in a category only.
    public static void saveGroupItemConfiguration(Context context, CategoryItem groupItem)
    {

        //Get the group item's preferences
        SharedPreferences groupItemPreferences = getGroupItemPreferences(context, groupItem.getSystem(), groupItem.getCategory(), groupItem.getGroup(), groupItem.getName());

        saveItemSettings(groupItem, groupItemPreferences);


    }

    //This method saves an item's configuration when the item is NOT in a group.
    public static void saveItemConfiguration(Context context, CategoryItem item)
    {

        //If the item passed is a CategoryGroup, handle it differently
        if(item instanceof CategoryGroup)
            saveGroupConfiguration(context, (CategoryGroup)item);

        //Create the shared preferences location for this item
        SharedPreferences itemPreferences = getItemPreferences(context, item.getSystem(), item.getCategory(), item.getName());

        saveItemSettings(item,itemPreferences);

    }

    public static CategoryItem loadItemConfiguration(Context context, System system, Category category, String itemName)
    {

        //Get the item's preferences
        SharedPreferences itemPreferences = getItemPreferences(context, system, category, itemName);

        //Create the item based off the type and ID saved in the Item's preferences.
        CategoryItem item = getTypeFromString(category, itemName, itemPreferences.getString(typeLocation, defaultType),
                itemPreferences.getLong(IDLocation, 123L));

        if(item instanceof CategoryGroup)
            item = loadGroupConfiguration(context, (CategoryGroup)item);
        else
            loadItemSettings(item, itemPreferences);

        return item;

    }

    private static void saveItemSettings(CategoryItem item, SharedPreferences itemPreferences)
    {

        SharedPreferences.Editor itemEditor = itemPreferences.edit();

        itemEditor.putString(typeLocation, getTypeString(item));
        itemEditor.putString(pdfDescLocation, item.getPdfDescription());
        itemEditor.putString(descLocation, item.getDescription());
        itemEditor.putString(hintLocation, item.getCommentHint());
        itemEditor.putLong(IDLocation, item.getID());

        if(item instanceof Slider) {

            //Use library to convert ordered list into json text
            Gson gson = new Gson();
            String jsonText = gson.toJson(((Slider)item).getContent());

            itemEditor.putString(sliderTextLocation, jsonText);
        }
        itemEditor.apply();

    }

    private static void loadItemSettings(CategoryItem item, SharedPreferences itemPreferences)
    {

        item.setDescription(itemPreferences.getString(descLocation, null));
        item.setPdfDescription(itemPreferences.getString(pdfDescLocation, null));
        item.setCommentHint(itemPreferences.getString(hintLocation, null));

        if(item instanceof Slider)
        {

            //Use library to get convert saved json text into ordered text list
            Gson gson = new Gson();
            String jsonText = itemPreferences.getString(sliderTextLocation, null);

            //Attempt to convert json text back into an ArrayList
            ArrayList<String> content = gson.fromJson(jsonText, ArrayList.class);

            //If the loaded text list from json is null, make it an empty list to avoid errors.
            if(content == null)
                content = new ArrayList<>();

            //Set the slider content
            ((Slider)item).setContent(content);

        }

    }

    //Gets a category's preferences
    //I made a method out of this so that repeated calls to get category preferences CAN'T be messed up.
    private static SharedPreferences getCategoryPreferences(Context context, System parent, String categoryName)
    {

        SharedPreferences categoryPrefereneces;

        if(parent.isSubSystem())
            categoryPrefereneces = context.getSharedPreferences(parent.getParentSystem().getDisplayName().toUpperCase() + "_SUBSYSTEM_" + parent.getDisplayName().toUpperCase() +
                    "_" + categoryName.toUpperCase(), Context.MODE_PRIVATE);
        else
            categoryPrefereneces = context.getSharedPreferences(parent.getDisplayName().toUpperCase() + "_" + categoryName.toUpperCase(), Context.MODE_PRIVATE);


        return categoryPrefereneces;

    }

    //Get's the preferences of any System
    private static SharedPreferences getSystemPreferences(Context context, String systemName, String parentSystemName, boolean isSubSystem)
    {

        SharedPreferences systemPreferences;

        if(isSubSystem && parentSystemName != null)
            systemPreferences = context.getSharedPreferences(parentSystemName.toUpperCase() + "_SUBSYSTEM_" + systemName.toUpperCase(), Context.MODE_PRIVATE);
        else
            systemPreferences = context.getSharedPreferences(systemName.toUpperCase(), Context.MODE_PRIVATE);

        return systemPreferences;

    }

    //Returns the preference location of a given item NOT inside a CategoryGroup
    private static SharedPreferences getItemPreferences(Context context, System system, Category category, String itemName)
    {

        SharedPreferences itemPreferences;

        if(system.isSubSystem())
            itemPreferences = context.getSharedPreferences(system.getParentSystem().getDisplayName().toUpperCase() + "_SUBSYSTEM_" + system.getDisplayName().toUpperCase()
            + "_" + category.getName().toUpperCase() + "_" + itemName.toUpperCase(), Context.MODE_PRIVATE);
        else
            itemPreferences = context.getSharedPreferences(system.getDisplayName().toUpperCase() + "_" + category.getName().toUpperCase() + "_" + itemName.toUpperCase(), Context.MODE_PRIVATE);

        return itemPreferences;

    }

    //Method to return the preferences location of an item located inside a CategoryGroup
    private static SharedPreferences getGroupItemPreferences(Context context, System system, Category category, CategoryItem group, String groupItemName)
    {

        SharedPreferences groupItemPreferences;

        if(system.isSubSystem())
            groupItemPreferences = context.getSharedPreferences(system.getParentSystem().getDisplayName().toUpperCase() + "_SUBSYSTEM_" + system.getDisplayName().toUpperCase()
            + "_" + category.getName().toUpperCase() + "_" + group.getName().toUpperCase() + "_" + groupItemName.toUpperCase(), Context.MODE_PRIVATE);
        else
            groupItemPreferences = context.getSharedPreferences(system.getDisplayName().toUpperCase() + "_" + category.getName().toUpperCase() + "_"
                + group.getName().toUpperCase() + "_" + groupItemName.toUpperCase(), Context.MODE_PRIVATE);

        return groupItemPreferences;

    }

    // Convert an item based off its type into a string
    private static String getTypeString(CategoryItem item)
    {

        if(item instanceof Checkbox)
            return "CHECKBOX";
        else if(item instanceof Numeric)
            return "NUMERIC";
        else if(item instanceof Slider)
            return "SLIDER";
        else if(item instanceof CategoryGroup)
            return "GROUP";
        else if(item instanceof DefectItem)
            return "DEFECT";
        else if(item instanceof RestrictionItem)
            return "RESTRICTION";
        else if(item instanceof ObservationItem)
            return "OBSERVATION";
        else
            return "";

    }

    //Create category items to populate categories using the types
    private static CategoryItem getTypeFromString(Category category, String name, String type, Long ID)
    {

        if(type.equals("CHECKBOX"))
            return new Checkbox(name, category, ID);
        else if(type.equals("NUMERIC"))
            return new Numeric(name, category, ID);
        else if(type.equals("SLIDER"))
            return new Slider(name, category, ID);
        else if(type.equals("GROUP"))
            return new CategoryGroup(name, category, ID);
        else if(type.equals("DEFECT"))
            return new DefectItem(name, category, ID);
        else if(type.equals("RESTRICTION"))
            return new RestrictionItem(name, category, ID);
        else if(type.equals("OBSERVATION"))
            return new ObservationItem(name, category, ID);
        else
            return null;

    }

    private static Category createCategoryFromString(System system, String categoryName)
    {

        //Names of categories are pre-defined
        if(categoryName.toUpperCase().equals("INFORMATION"))
            return new Information(system);
        else if(categoryName.toUpperCase().equals("OBSERVATION"))
            return new Observations(system);
        else if(categoryName.toUpperCase().equals("RESTRICTION"))
            return new Restrictions(system);
        else if(categoryName.toUpperCase().equals("DEFECT"))
            return new Defect(system);
        else if(categoryName.toUpperCase().equals("SUB SYSTEMS"))
            return new Sub_System(system);
        else if(categoryName.toUpperCase().equals("CONTEXT MEDIA"))
            return new Media(system);
        else
            return null;

    }

}
