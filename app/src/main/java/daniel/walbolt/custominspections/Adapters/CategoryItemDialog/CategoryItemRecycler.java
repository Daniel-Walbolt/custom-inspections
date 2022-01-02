package daniel.walbolt.custominspections.Adapters.CategoryItemDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Dialogs.Creators.CategoryItemDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.R;

public class CategoryItemRecycler extends RecyclerView.Adapter<CategoryItemRecycler.ItemHolder>
{

    // The CategoryItem recycler creates a list of CategoryItems that can each have their own behavior using pop-up dialogs.
    // This recycler is used in both the category dialog and the system-page itself.

    private ArrayList<CategoryItem> items;

    private boolean isInDialog; // If this is true, the items are being displayed in an editor dialog, and should not have normal functionality
    private CategoryDialog categoryDialog; // Dialog reference for creation of new dialogs (otherwise they're small if we use context of a recycler item)

    public CategoryItemRecycler(ArrayList<CategoryItem> items)
    {

        this.items = items;

    }

    public CategoryItemRecycler(ArrayList<CategoryItem> items, CategoryDialog categoryDialog)
    {

        this.items = items;
        this.isInDialog = true;
        this.categoryDialog = categoryDialog;

    }

    //Override this method because we need to load two types of layouts (this can be extended to more)!
    //One layout is a text view and a nested recycler view (CategoryGroup).
    // Otherwise, the default layout is just a CheckBox item.
    @Override
    public int getItemViewType(int position)
    {

        if(items.get(position) instanceof CategoryGroup)
            return 1;
        else
            return 0;

    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View v = null;

        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_group, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item_recycler, parent, false);
        }

        return new ItemHolder(v, viewType); // Initialize the view

    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position)
    {

        //Get the item associated with the view
        CategoryItem item = items.get(position);

        if(isInDialog)
        {

            //IF the recycler view is inside the category dialog, clicking on any item will start editing it
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryDialog.editCategoryItem(item);
                }
            });

        }

        //Initialize the views based off the type of the item
        if(item instanceof CategoryGroup)
            initCategoryGroup(holder, (CategoryGroup) item);
        else
            initCheckBoxItem(holder, item);

    }

    private void initCheckBoxItem(ItemHolder holder, CategoryItem item)
    {

        //Implement checkbox functionality
        holder.checkBox.setChecked(item.isApplicable()); // Set the current state of the item (in the event this item is reloaded)

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isInDialog) // If the recycler is in a dialog, remove checkbox functionality
                    buttonView.setChecked(false);
                else
                {

                    item.setApplicability(isChecked);
                    if(isChecked) // When the item is set as APPLICABLE, call its method for it
                        item.onChecked(); // Call the method the individual CategoryItem defines

                }

            }
        });

        //Find and set the name of the CategoryItem
        holder.name = holder.itemView.findViewById(R.id.category_item_recycler_name);
        holder.name.setText(item.getName());

        //Initialize comment button
        holder.comments = holder.itemView.findViewById(R.id.category_item_recycler_comment);

        //If the recycler is in a dialog, hide the comment button
        if(isInDialog)
            holder.comments.setVisibility(View.GONE);
        else
            //Give comment button functionality (when its visible)
            holder.comments.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    item.openCommentDialog(holder.itemView.getContext());
                }
            });


    }

    private void initCategoryGroup(ItemHolder holder, CategoryGroup item)
    {

        // Find and set the name of this group
        holder.name.setText(item.getName());

        //Initialize the groups recyclerview, and populate it
        if(isInDialog)
            item.initDialogRecycler(holder.itemRecycler, categoryDialog);
        else
            item.initRecycler(holder.itemRecycler); // Initialize the recycler to display the group's contents

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder
    {

        //Every checkbox item has these views
        TextView name;
        CheckBox checkBox;
        ImageButton comments;

        //In the case the CategoryItem is a CategoryGroup
        RecyclerView itemRecycler;


        public ItemHolder(@NonNull View itemView, int viewType)
        {

            super(itemView);
            if(viewType == 1) //  The category item is a CategoryGroup
            {

                //Find CategoryGroup views
                name = itemView.findViewById(R.id.category_group_title);
                itemRecycler = itemView.findViewById(R.id.category_group_container);

            }
            else if(viewType == 0)
            {

                //Initialize the views for CategoryItems
                name = itemView.findViewById(R.id.category_item_recycler_name);
                comments = itemView.findViewById(R.id.category_item_recycler_comment);
                checkBox = itemView.findViewById(R.id.category_item_recycler_checkbox);

            }

        }

    }

}
