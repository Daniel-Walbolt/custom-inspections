package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Adapters.CategoryItemDialog.CategoryItemRecycler;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CommentDialog;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.System;

public abstract class Category
{

    /*

    Categories are objects that manipulate their own View object. Each one is essentially a ViewModel, that may interact with a BusinessModel.

    Categories, from the user standpoint, are a group by which the user can store CategoryItems. Categories group together information, and are mostly customizable.

    Categories save the information they store in the inspection file located in the database. However, the categories that DO exist will always exist until deleted. If information exists in
    an inspection file for a category that no longer exists, this information must be accounted for, notify the user, and dispatched. A subsequent save of the inspection file will
    NOT have this old information.

    There can only be 1 of each category type in each System.

     */

    private String name;
    private System parentSystem;

    public ArrayList<CategoryItem> categoryItems;

    RecyclerView categoryRecycler;

    public Category(Category.TYPE type, System parent)
    {

        this.name = type.getDisplayName();
        this.parentSystem = parent;

        categoryItems = new ArrayList<>();

    }

    Category thisClass() // Necessary? for the edit button click listener when it opens the Category Dialog
    {

        return this;

    }

    public void initRecycler()
    {

        //The information category loads basic CategoryItems (Check Boxes, Sliders, Numerics, Groups)
        //The CategoryItemRecycler handles groups as well as other items
        CategoryItemRecycler adapter = new CategoryItemRecycler(categoryItems);
        LinearLayoutManager manager = new LinearLayoutManager(categoryRecycler.getContext(), RecyclerView.VERTICAL, false);
        categoryRecycler.setAdapter(adapter);
        categoryRecycler.setLayoutManager(manager);
        categoryRecycler.setNestedScrollingEnabled(false);

    }

    private void sortItems()
    {

        //Sort the items so that all items without groups come before the groups.
        // Groups should be sorted alphabetically by name
        // Items will be sorted alphabetically as well.

        ArrayList<CategoryItem> items = new ArrayList<>();
        ArrayList<CategoryGroup> groups = new ArrayList<>();

        //Separate groups and items
        for(CategoryItem item : categoryItems)
            if(item instanceof CategoryGroup)
                groups.add((CategoryGroup)item);
            else
                items.add(item);


        for (int j = items.size() - 1; j > 0; j--)
        {

            //If the item that comes next (down the list) is alphabetically LATER
            //then switch the items. This for loop will move every element from the end towards the front as far as it should go
            if(j-1 >= 0)
                if(items.get(j).getName().compareTo(items.get(j-1).getName()) < 0)
                {

                    CategoryItem temp = items.get(j);
                    items.set(j, items.get(j-1));
                    items.set(j-1, temp);

                }

        }


        for (int j = groups.size() - 1; j > 0; j--) {

            //If the item that comes next (down the list) is alphabetically LATER
            //then switch the items. This for loop will move every element from the end towards the front as far as it should go
            if (j - 1 >= 0)
                if (groups.get(j).getName().compareTo(groups.get(j - 1).getName()) < 0) {

                    CategoryGroup temp = groups.get(j);
                    groups.set(j, groups.get(j - 1));
                    groups.set(j - 1, temp);

                }

        }

        categoryItems.clear();
        categoryItems.addAll(items);
        categoryItems.addAll(groups);

    }

    //Update the content in this Category's Recycler
    public void updateRecycler()
    {

        if(categoryRecycler != null)
            if(categoryRecycler.getAdapter() != null)
                categoryRecycler.getAdapter().notifyDataSetChanged();

    }



    public System getSystem()
    {

        return parentSystem;

    }

    public String getName()
    {

        return name;

    }

    public void addItem(CategoryItem item)
    {

        categoryItems.add(item);
        sortItems();

    }

    public ArrayList<CategoryItem> getCategoryItems()
    {

        return categoryItems;

    }

    //Delete this category from the system, this should remove it from the system's category list AND reload the page.
    public void delete()
    {

        parentSystem.removeCategory(this);

    }

    public int getMediaCount()
    {

        int mediaCount = 0;

        for(CategoryItem item : categoryItems)
            mediaCount += item.getMediaCount();

        return mediaCount;

    }

    public int getCommentCount()
    {

        int commentCount = 0;
        for(CategoryItem item : categoryItems)
            commentCount += item.getCommentCount();

        return commentCount;

    }

    public abstract void loadToPage(LinearLayout pageLayout); // Method used to load the category, and its items on to the page layout.

   /* public Map<String, Object> save(InspectionData savingTo) // To Database
    {

        // TODO: Save to the database

    }

    public void loadFrom(Context context, Map<String, Object> inspectorCheckableData)
    {

        // TODO: Load from the database

    }*/

    public enum TYPE {
        INFORMATION {
            @Override
            public String getDisplayName() {
                return "Information";
            }
        },
        DEFECT {
            @Override
            public String getDisplayName() {
                return "Defect";
            }
        },
        MEDIA {
            @Override
            public String getDisplayName() {
                return "Context Media";
            }
        },
        OBSERVATION {
            @Override
            public String getDisplayName() {
                return "Observation";
            }
        },
        RESTRICTION {
            @Override
            public String getDisplayName() {
                return "Restriction";
            }
        },
        SETTINGS {
            @Override
            public String getDisplayName() {
                return "Settings";
            }
        },
        SUB_SYSTEMS {
            @Override
            public String getDisplayName() {
                return "Sub Systems";
            }
        };

        public abstract String getDisplayName();

    }

}
