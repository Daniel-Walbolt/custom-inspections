package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.CategoryItemDialog.CategoryItemRecycler;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.R;

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

    //Adds a category item to this group
    public void addItem(CategoryItem item)
    {

        this.items.add(item);
        item.setGroup(this);

    }

    //Convenient method to move a category item from another place to this group
    public void moveItem(ArrayList<CategoryItem> from, CategoryItem item)
    {

        from.remove(item);
        items.add(item);

    }

    //Removes the CategoryItem from this group.
    public boolean removeItem(CategoryItem targetItem)
    {

        if(this.items.remove(targetItem)) {
            targetItem.setGroup(null);
            return true;
        }
        else
            return false;

    }

    //Initialize the recycler that displays this group's contents
    //This is called by a CategoryItemRecycler adapter whenever the item is this group.
    public void initRecycler(RecyclerView groupRecycler)
    {

        CategoryItemRecycler adapter = new CategoryItemRecycler(this.items); // Create a CategoryItem recycler using the group's items.
        LinearLayoutManager manager = new LinearLayoutManager(groupRecycler.getContext(), LinearLayoutManager.VERTICAL, false);
        groupRecycler.setAdapter(adapter); // Set the recycler's adapter as the one just created
        groupRecycler.setLayoutManager(manager); // Set the recycler's layout manager to the one just created
        groupRecycler.setNestedScrollingEnabled(false); // Disable scrolling of the group's contents (the entire page scrolls!)

    }

    //Initialize the recycler that displays this group's contents
    //This is called by a CategoryItemRecycler adapter whenever the item is this group.
    // This method is used when the RecyclerView is in a dialog.
    public void initDialogRecycler(RecyclerView groupRecycler, CategoryDialog dialog)
    {

        CategoryItemRecycler adapter = new CategoryItemRecycler(this.items, dialog); // Create a CategoryItem recycler using the group's items.
        LinearLayoutManager manager = new LinearLayoutManager(groupRecycler.getContext(), LinearLayoutManager.VERTICAL, false);
        groupRecycler.setAdapter(adapter); // Set the recycler's adapter as the one just created
        groupRecycler.setLayoutManager(manager); // Set the recycler's layout manager to the one just created
        groupRecycler.setNestedScrollingEnabled(false); // Disable scrolling of the group's contents (the entire page scrolls!)


    }

    @Override
    public CategoryGroup getGroup() {
        return null;
    }

    @Override
    public void onChecked() {
        //This category item is never "checked" so do nothing.
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
    public boolean hasAutoOpenComments() {
        return super.hasAutoOpenComments();
    }

    @Override
    public boolean hasComments() {
        return super.hasComments();
    }

    @Override
    public void addPicture(InspectionMedia picture) {
        super.addPicture(picture);
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
    public void setHasComments(boolean hasComments, boolean autoOpenComments) {
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

    @Override
    public void load(LinearLayout categoryLayout)
    {

        View categoryGroupLayout = LayoutInflater.from(categoryLayout.getContext()).inflate(R.layout.category_group, categoryLayout, false);

        //Initialize category group views
        TextView title = categoryGroupLayout.findViewById(R.id.category_group_title);
        title.setText(getName());
        LinearLayout container = categoryGroupLayout.findViewById(R.id.category_group_container);

        //Load category items contained within this group
        for(CategoryItem item : items)
            item.load(container);

        //Add this category group to the category
        categoryLayout.addView(categoryGroupLayout);

    }

}
