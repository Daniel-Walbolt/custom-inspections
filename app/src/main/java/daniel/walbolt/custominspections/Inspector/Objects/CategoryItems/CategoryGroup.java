package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Adapters.CategoryItemRecycler;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Pages.Main;

public class CategoryGroup extends CategoryItem
{

    /*

    CategoryGroups are a way to organize Category Items. These are very similar to a Category, but must be loaded inside a category.

    CategoryGroups simply are a Linear Layout with a text view to display their name. They serve no other purpose but to organize the data the user may put inside them.

    In the case where a Category Item is not placed inside a group, all items should be displayed before any groups. Groups will be displayed in alphabetical order.

    CategoryGroups can not be in other CategoryGroups, so every other CategoryItem must have a "group" variable that references their group.

     */

    private ArrayList<CategoryItem> items;

    public CategoryGroup(String name, Category category) {
        super(name, category);
        items = new ArrayList<>();
    }

    public CategoryGroup(String name, Category category, long ID) {
        super(name, category, ID);
        items = new ArrayList<>();
    }

    //Adds a category item to this group
    public void addItem(CategoryItem item)
    {

        this.items.add(item);
        item.setGroup(this);

        //Sort the newly added item
        sortItems();

    }

    @Override
    public Map<String, Object> save(InspectionData saveTo)
    {

        Map<String, Object> data = new HashMap<>();

        data.put("ID", getID());

        ArrayList<Map<String, Object>> itemData = new ArrayList<>();

        for (CategoryItem groupItem : items)
        {

            itemData.add(groupItem.save(saveTo));

        }

        data.put("Items", itemData);

        return data;

    }

    @Override
    public void loadFrom(Context context, Map<String, Object> groupData)
    {

        //A group must also load its items data:
        ArrayList<Map<String, Object>> allItemData = new ArrayList<>();

        if (groupData.containsKey("Items"))
            allItemData = (ArrayList<Map<String, Object>>) groupData.get("Items");

        //This is the same algorithm that is in the category loadFrom method.
        for (Map<String, Object> itemData : allItemData)
        {

            //Get the ID from the item data
            if (itemData.containsKey("ID"))
            {

                long ID =  (long) itemData.get("ID");

                //Loop through every item in the category and find the item with the same ID
                for (CategoryItem item : items)
                {

                    if (item.getID() == ID)
                        item.loadFrom(context, itemData);

                }

            }

        }

    }

    //Removes the CategoryItem from this group.
    public boolean removeItem(Context context, CategoryItem targetItem)
    {

        //If we removed the item passed from this group,
        if(this.items.remove(targetItem)) {

            //set the item's group to null
            targetItem.setGroup(null);

            //Delete item configuration
            if(!Main.inspectionSchedule.isPastInspection)
                Configuration.deleteItemConfiguration(context, targetItem);

            return true;
        }
        else
            return false;

    }

    //Initialize the recycler that displays this group's contents
    //This is called by a CategoryItemRecycler adapter whenever the item is this group.
    public void initRecycler(RecyclerView groupRecycler, TextView emptyView)
    {

        CategoryItemRecycler adapter = new CategoryItemRecycler(this.items, emptyView,groupRecycler ); // Create a CategoryItem recycler using the group's items.
        LinearLayoutManager manager = new LinearLayoutManager(groupRecycler.getContext(), LinearLayoutManager.VERTICAL, false);
        groupRecycler.setAdapter(adapter); // Set the recycler's adapter as the one just created
        groupRecycler.setLayoutManager(manager); // Set the recycler's layout manager to the one just created
        groupRecycler.setNestedScrollingEnabled(false); // Disable scrolling of the group's contents (the entire page scrolls!)

    }

    //Initialize the recycler that displays this group's contents
    //This is called by a CategoryItemRecycler adapter whenever the item is this group.
    // This method is used when the RecyclerView is in a dialog.
    public void initDialogRecycler(RecyclerView groupRecycler, CategoryDialog dialog, TextView emptyView)
    {

        CategoryItemRecycler adapter = new CategoryItemRecycler(this.items, dialog, emptyView, groupRecycler); // Create a CategoryItem recycler using the group's items.
        LinearLayoutManager manager = new LinearLayoutManager(groupRecycler.getContext(), LinearLayoutManager.VERTICAL, false);
        groupRecycler.setAdapter(adapter); // Set the recycler's adapter as the one just created
        groupRecycler.setLayoutManager(manager); // Set the recycler's layout manager to the one just created
        groupRecycler.setNestedScrollingEnabled(false); // Disable scrolling of the group's contents (the entire page scrolls!)


    }

    public ArrayList<CategoryItem> getItems()
    {

        return items;

    }

    private void sortItems()
    {

        for (int j = items.size() - 1; j > 0; j--)
        {

            //If the item that comes next (down the list) is alphabetically LATER
            //then switch the items. This for loop will move the most recently added item up the list as far as it should go.
            if(j-1 >= 0)
                if(items.get(j).getName().compareTo(items.get(j-1).getName()) < 0)
                {

                    CategoryItem temp = items.get(j);
                    items.set(j, items.get(j-1));
                    items.set(j-1, temp);

                }

        }

    }

    @Override
    public CategoryGroup getGroup() {
        return null;
    }

    @Override
    public ArrayList<InspectionMedia> getPictures() {
        return null;
    }

    @Override
    public InspectionMedia getCommentMedia() {
        return null;
    }

    @Override
    public int getMediaCount()
    {

        int mediaCount = 0;

        for(CategoryItem item : items)
            mediaCount += item.getMediaCount();

        return mediaCount;

    }

    @Override
    public boolean hasComments() {
        return super.hasComments();
    }

    @Override
    public void addMedia(InspectionMedia picture) {
        super.addMedia(picture);
    }

    @Override
    public int getCommentCount() {

        int commentCount = 0;

        for(CategoryItem item : items)
            commentCount += item.getCommentCount();

        return commentCount;

    }

    @Override
    public void removeCommentMedia() {
    }

    @Override
    public boolean isApplicable() {
        return false;
    }

    @Override
    public void setApplicability(boolean applicability) { }

    @Override
    public void openCommentDialog(Context context) { }

    @Override
    public boolean hasPictures() {
        return false;
    }

}
