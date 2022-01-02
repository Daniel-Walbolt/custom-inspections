package daniel.walbolt.custominspections.Inspector.Dialogs.Editors;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import daniel.walbolt.custominspections.Adapters.CategoryItemDialog.CategoryItemRecycler;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.Inspector.Dialogs.Creators.CategoryItemDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.R;

public class CategoryDialog extends Dialog
{

    /*

    The CategoryDialog edits a category

    Here, we can delete a category, or add/delete/edit its CategoryItems

     */

    private RecyclerView itemRecycler; // Recycler for the category's items

    private Category category; // The category being edited

    public CategoryDialog(@NonNull Context context, Category category)
    {

        super(context);
        this.category = category;

        //Set the view and settings of the dialog
        setContentView(R.layout.category_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);

        //Initialize the content of the dialog
        initButtons();
        initCategoryItemRecycler();

        //Show the dialog to the user
        show();

    }

    private void initButtons()
    {

        //Find and set the name of the category to the top of the dialog
        TextView name = findViewById(R.id.category_dialog_name);
        name.setText(category.getName());

        //Initialize the functionality of the addItem button
        Button addItem = findViewById(R.id.category_dialog_add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CategoryItemDialog(getContext(), category).setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        itemRecycler.getAdapter().notifyDataSetChanged(); // Update the category item recycler
                    }
                });
            }
        });

        //Initialize the functionality of the delete category button
        Button delete = findViewById(R.id.category_dialog_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmAlert(getContext(), "Are you sure you want to delete this " + category.getName() + " category?\nThis action is irreversible.", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteThisCategory();
                    }
                });
            }
        });



    }

    //Initialize the recycler for the CategoryItems
    private void initCategoryItemRecycler()
    {

        //Find the RecyclerView of this dialog
        itemRecycler = findViewById(R.id.category_dialog_items_recycler);

        // Create a CategoryItemRecycler adapter that is in EDIT MODE.
        // Items in this dialog should not behave the same way they do on a system page.
        CategoryItemRecycler adapter = new CategoryItemRecycler(category.getCategoryItems(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        itemRecycler.setAdapter(adapter);
        itemRecycler.setLayoutManager(manager);
        itemRecycler.setNestedScrollingEnabled(true);

    }

    //This method is called from the recycler view, and the item is somewhere in the category being edited.
    public void editCategoryItem(CategoryItem item)
    {

        new CategoryItemDialog(getContext(), category, item).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                itemRecycler.getAdapter().notifyDataSetChanged(); // After editing, update the category's items
            }
        });

    }

    private void deleteThisCategory()
    {

        category.delete();

    }

}
