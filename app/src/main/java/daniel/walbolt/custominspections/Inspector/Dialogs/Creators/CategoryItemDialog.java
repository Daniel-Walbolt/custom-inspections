package daniel.walbolt.custominspections.Inspector.Dialogs.Creators;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ErrorAlert;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.TextArrayDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.TextInputDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.DescriptionDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Defect;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Information;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Observations;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Restrictions;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Checkbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.DefectItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.ObservationItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.RestrictionItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Slider;
import daniel.walbolt.custominspections.R;

public class CategoryItemDialog extends Dialog
{

    private TextView title;
    private ImageButton dialogInfo;

    private EditText name; // Name of the item
    private RelativeLayout typeSettings;
    private Spinner typeSpinner; // Type of the item (for creation, NOT editing)

    private Spinner groupSpinner; // Selector for the group the item belongs to
    private RelativeLayout groupSettings;

    /*
    Category Item settings
     */

    private LinearLayout allSettings; // Contains all views pertaining to settings (should be hidden when creating)

    //Comment Settings
    private TextView setDescription;
    private TextView setHint;
    private TextView setPDFDesc;

    //Group Settings
    private String groupTarget; // Temporary value holder for the desired group
    private ArrayList<String> groupArray; // List to store the groups in the category

    //Slider settings
    private TextView editSlider;

    //Numeric Settings
    private TextView editNumeric;

    //The category that is being changed, only needed when CREATING an item
    private Category category;
    private HashMap<String, CategoryGroup> groups; // HashMap that uses item's name as the key, and the item itself as the value. keySet() is used in this dialog.

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
        initViews();

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
        initViews();

        show();

    }

    private void initViews()
    {

        /*
        Find the views of the Dialog
         */

        title = findViewById(R.id.category_item_dialog_title);;
        name = findViewById(R.id.category_item_name);
        typeSpinner = findViewById(R.id.category_item_dialog_type);

        typeSettings = findViewById(R.id.categoy_item_dialog_type_content);
        groupSettings = findViewById(R.id.category_item_dialog_group_content);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem().equals("Group"))
                    groupSettings.setVisibility(View.GONE);
                else
                    groupSettings.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //This should never happen.
            }
        });
        fillTypeOptions(); // Fill the Type SpinnerView with the appropriate types based on the category

        groupSpinner = findViewById(R.id.category_item_dialog_group);

        dialogInfo = findViewById(R.id.category_item_dialog_info); // Information icon to display additional information
        dialogInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DescriptionDialog(getContext(),
                        "Sections are what hold information during an inspection. " +
                        "Name them wisely, and group them in a way that will help you organize!\n\n" +
                        "Note: Groups can not be in another group.\n" +
                        "You can not change the type (Check Box, etc.) when editing!");
            }
        });

        initSettings(); // Initialize the views that control the settings of a CategoryItem

        Button finish = findViewById(R.id.category_item_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkName()) {
                    createCategoryItem();
                    dismiss();
                }
            }
        });

        Button delete = findViewById(R.id.category_item_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConfirmAlert(view.getContext(), "Deleting this section is irreversible.", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        editItem.delete(view.getContext());
                        dismiss();
                    }
                });
            }
        });

        /*
        Initialize the views of the dialog based on the dialog state
         */

        initState();

    }

    //This method controls how certain views are initialized based on the state of the dialog.
    // There are only 2 possible states: create or edit.
    private void initState()
    {

        groupArray = new ArrayList<>(groups.keySet()); // Initialize the group array

        if(isEditing)
            initEditState();
        else
        {

            /*
            Functions to perform only when NOT editing
             */

            //Fill the group spinner with a list made from all the GROUPS already in the CATEGORY being edited
            groupArray.add(0, "None"); // The option to be in no group is default

            //Settings are only visible when editing an item.
            allSettings.setVisibility(View.GONE);


        }

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

    }

    private void initSettings()
    {

        allSettings = findViewById(R.id.category_item_dialog_settings);

        setDescription = findViewById(R.id.category_item_dialog_set_description);
        setDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputDialog textInputDialog = new TextInputDialog(getContext(), "Edit Description",
                        "This description is only displayed to you when filling out a report.\nIt can be accessed by pressing the info. icon on a section. ",
                        editItem.getDescription() != null ? editItem.getDescription() : "");
                textInputDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        editItem.setDescription(textInputDialog.getText());
                        Toast.makeText(getContext(), "Edited Description", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }); // Nested listener to get input using a dialog
        setHint = findViewById(R.id.category_item_dialog_set_comment_hint);
        setHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputDialog textInputDialog = new TextInputDialog(getContext(), "Edit Hint",
                        "This hint is only displayed to you when opening a Comment Editor.\nIt should describe what 'topic' you want described in the comments.",
                        editItem.getCommentHint() != null ? editItem.getCommentHint() : "");
                textInputDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        editItem.setCommentHint(textInputDialog.getText());
                        Toast.makeText(getContext(), "Edited Comment Hint", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        setPDFDesc = findViewById(R.id.category_item_dialog_set_pdf_description);
        setPDFDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputDialog textInputDialog = new TextInputDialog(getContext(), "Edit PDF Description",
                        "This description is displayed to you AND CLIENTS in the final PDF.\nThis description is used to describe what this section means to a client.",
                        editItem.getPdfDescription() != null ? editItem.getPdfDescription() : "");
                textInputDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        editItem.setPdfDescription(textInputDialog.getText());
                        Toast.makeText(getContext(), "Edited PDF Description", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        editSlider = findViewById(R.id.category_item_dialog_edit_slider);

        editNumeric = findViewById(R.id.category_item_dialog_edit_numeric);

    }

    //This method is only called if an item is being edited by the user
    private void initEditState()
    {

        title.setText("Edit Section");

        name.setText(editItem.getName()); //Set the name field as the current name of the item we're editing

        typeSettings.setVisibility(View.GONE); // An item's type can not be changed because TYPE-specific information would be lost. (Slider progress, etc.)

        //Check if the item we are editing is a CategoryGroup.
        // CategoryGroups can not be in another group.
        if(editItem instanceof CategoryGroup) {
            groupSettings.setVisibility(View.GONE);
            allSettings.setVisibility(View.GONE);
        }
        //Get the group which the item we are editing belongs to. If the group is null, it is "None"
        String groupName = editItem.getGroup() != null ? editItem.getGroup().getName() : "None";

        groupArray.remove(groupName); // Remove the item's group from the list
        groupArray.add(0, groupName); // Set the item's group to come first

        if(!groupName.equals("None"))
            groupArray.add("None"); // Move the "None" option to the end of the list if it is not first.

        allSettings.setVisibility(View.VISIBLE);

        editSlider.setVisibility(editItem instanceof Slider ? View.VISIBLE : View.GONE); // Show slider settings if this section is a slider.
        editSlider.setOnClickListener(v -> new TextArrayDialog(getContext(),((Slider)editItem).getContent()));;

        editNumeric.setVisibility(editItem instanceof Numeric ? View.VISIBLE : View.GONE);
        editNumeric.setOnClickListener(v -> {
            TextInputDialog input = new TextInputDialog(getContext(), "Numeric Unit", "The unit that describes the number being entered",  ((Numeric)editItem).getUnit());
            input.setOnDismissListener(e -> {
                ((Numeric)editItem).setUnit(input.getText());
            });
        });

    }

    //This method returns the desired type of the item being created.
    //Types depend on the category the item is being created in
    private CategoryItem getType()
    {

        //Every category can have groups in them, so first check if the type is a CategoryGroup
        if (typeSpinner.getSelectedItem().equals("Group")) {
            groupTarget = "None"; // A group can not be inside of another group.
            return new CategoryGroup(name.getText().toString(), category);
        }
        else if(category instanceof Information)
        {

            //Check for Info-item types
            if(typeSpinner.getSelectedItem().equals("Check Box"))
                return new Checkbox(name.getText().toString(), category);
            else if(typeSpinner.getSelectedItem().equals("Slider"))
                return new Slider(name.getText().toString(), category);
            else if(typeSpinner.getSelectedItem().equals("Numeric Entry"))
                return new Numeric(name.getText().toString(), category);

        }
        else if(category instanceof Restrictions)
        {

            //Check for restriction item types
            if(typeSpinner.getSelectedItem().equals("Restriction"))
                return new RestrictionItem(name.getText().toString(), category);

        }
        else if(category instanceof Observations)
        {

            if(typeSpinner.getSelectedItem().equals("Observation"))
                return new ObservationItem(name.getText().toString(), category);

        }
        else if(category instanceof Defect)
        {

            if(typeSpinner.getSelectedItem().equals("Defect"))
                return new DefectItem(name.getText().toString(), category);

        }

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
                editItem.getGroup().removeItem(getContext(), editItem);
            else
                editItem.getCategory().removeItem(getContext(), editItem);

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

    //This method makes sure the name passes certain parameters
    private boolean checkName()
    {

        String inputName = name.getText().toString();

        //The name must NOT be nothing, or "".
        if(inputName.replaceAll(" ", "").isEmpty()) // Replace all spaces so names like "    " don't pass this test either
        {

            new ErrorAlert(getContext(), "The name of the section can not be empty!");
            return false;

        }

        for(CategoryItem item : category.getCategoryItems())
        {

            //If the item in the category can contain more items, loop through it too
            if(item instanceof CategoryGroup)
            {

                for(CategoryItem groupItem : ((CategoryGroup)item).getItems())
                {

                    if(groupItem.getName().equalsIgnoreCase(inputName))
                    {

                        if(groupItem != editItem)
                        {

                            new ErrorAlert(getContext(), "The name of the section is already used in this category!");
                            return false;

                        }


                    }

                }

            }
            else if(item.getName().equals(inputName))
            {

                if(item != editItem)
                {

                    new ErrorAlert(getContext(), "The name of the section is already used in this category!");
                    return false;

                }

            }

        }

        return true;

    }

    private void fillTypeOptions()
    {

        //ArrayList stores the values of the Spinner view.
        ArrayList<String> types = new ArrayList<>();

        //Fill the list based on the type of category it is
        if(category instanceof Information)
        {

            types.addAll(Arrays.asList("Check Box", "Slider", "Numeric Entry"));

        }
        else if(category instanceof Observations)
            types.add(0, "Observation");
        else if(category instanceof Restrictions)
            types.add(0, "Restriction");
        else if(category instanceof Defect)
            types.add(0, "Defect");

        types.add("Group"); // Every category has the ability to render Groups

        //Set the adapter to the Spinner view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

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
