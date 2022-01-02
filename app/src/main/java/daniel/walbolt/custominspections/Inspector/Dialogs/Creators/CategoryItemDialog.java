package daniel.walbolt.custominspections.Inspector.Dialogs.Creators;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Checkbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Slider;
import daniel.walbolt.custominspections.R;

public class CategoryItemDialog extends Dialog
{

    private EditText name;
    private Spinner type;
    private Spinner groupSpinner;

    private String groupTarget;

    //The category that is being changed, only needed when CREATING an item
    private Category category;
    private HashMap<String, CategoryGroup> groups;

    //The edit target when this dialog is used to EDIT instead of CREATE
    private CategoryItem editItem;
    private boolean isEditing = false;

    public CategoryItemDialog(@NonNull Context context, Category editTarget)
    {

        super(context);
        this.category = editTarget;

        setContentView(R.layout.category_item_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        getGroups(); // Get the category groups
        initButtons();

        show();

    }

    //Alternative constructor when this dialog is EDITING an item instead of CREATING one
    public CategoryItemDialog(@NonNull Context context, Category editTarget, CategoryItem editItem)
    {

        super(context);
        this.category = editTarget;
        this.editItem = editItem;

        setContentView(R.layout.category_item_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        isEditing = true;

        getGroups();
        initButtons();

        show();

    }

    private void initButtons()
    {

        TextView title = findViewById(R.id.category_item_dialog_title);;
        if(isEditing)
            title.setText("Edit Section");

        //Find the views of the dialog
        name = findViewById(R.id.category_item_name);
        if(isEditing)
            name.setText(editItem.getName()); //Set the name field as the current name of the item we're editing

        type = findViewById(R.id.category_item_type);

        if(isEditing) // If editing, the user can not change the TYPE of CategoryItem. For many reasons.
            type.setVisibility(View.GONE);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem().equals("Group"))
                    groupSpinner.setVisibility(View.GONE);
                else
                    groupSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This should never happen.
            }
        });


        groupSpinner = findViewById(R.id.category_item_group);
        //Fill the group spinner with a custom list made from all the GROUPS already in the CATEGORY being edited
        ArrayList<String> groupArray = new ArrayList<>(groups.keySet());
        if(isEditing) // If we are editing an item, the default group should be ITS group.
        {

            //Check if the item we are editing is a CategoryGroup.
            // CategoryGroups can not be in another group.
            if(editItem instanceof CategoryGroup)
                groupSpinner.setVisibility(View.GONE);

            //Get the group which the item we are editing belongs to. If the group is null, it is "None"
            String groupName = editItem.getGroup() != null ? editItem.getGroup().getName() : "None";

            groupArray.remove(groupName); // Remove the item's group from the list
            groupArray.add(0, groupName); // Set the item's group to come first

            if(!groupName.equals("None"))
                groupArray.add("None"); // Move the "None" option to the end of the list if it is not first.



        }
        else
            groupArray.add(0, "None"); // The option to be in no group is default when NOT editing.

        groupTarget = groupArray.get(0); // The default groupTarget is the first entry in the array.

        //Set the adapter to the Spinner view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, groupArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        //Create a listener for whenever the group spinner changes value.
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                groupTarget = (String) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // This is impossible
            }
        });





        Button finish = findViewById(R.id.category_item_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCategoryItem();
                dismiss();
            }
        });

    }

    private CategoryItem getType()
    {

        if(type.getSelectedItem().equals("Check Box"))
            return new Checkbox(name.getText().toString(), category);
        else if(type.getSelectedItem().equals("Slider"))
            return new Slider(name.getText().toString(), category);
        else if(type.getSelectedItem().equals("Numeric Entry"))
            return new Numeric(name.getText().toString(), category);
        else if (type.getSelectedItem().equals("Group")) {
            groupTarget = "None"; // A group can not be inside of another group.
            return new CategoryGroup(name.getText().toString(), category);
        }else
            return null;

    }

    //This method is called by pressing the "Finish" button.
    private void createCategoryItem()
    {

        CategoryItem item;

        // If we're editing an item, we already have an item with a type.
        if(isEditing)
        {

            item = editItem; // Set the item to be relocated or renamed to the item being edited.
            item.setName(name.getText().toString()); // Set the name to the name in the dialog

            //Remove the old item from the group/category it is in.
            if(item.getGroup() != null)
                editItem.getGroup().removeItem(editItem);
            else
                editItem.getCategory().getCategoryItems().remove(editItem);

        }
        else
            item = getType();

        //Add the item to its appropriate group
        if(item == null)
            Toast.makeText(getContext(), "You must select a type", Toast.LENGTH_SHORT).show();
        if(!groupTarget.equals("None"))
            groups.get(groupTarget).addItem(item); // Add this new category item to the desired group.
        else
            category.addItem(item); // If the item has no group, just add it to the category


        //Finish creating the item with a confirmation to the user
        if(isEditing)
            Toast.makeText(getContext(), "Edited item", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Created new item", Toast.LENGTH_SHORT).show();

    }

    private void getGroups()
    {

        groups = new HashMap<>();

        for(CategoryItem item : category.getCategoryItems())
        {

            if(item instanceof CategoryGroup)
                groups.put(item.getName(), (CategoryGroup) item);

        }

    }

}
