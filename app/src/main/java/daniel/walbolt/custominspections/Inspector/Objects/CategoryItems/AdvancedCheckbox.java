package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;

/*
The AdvancedCheckbox is more robust than the traditional Checkbox. This item can create multiple checkboxes, and provide basic relationships between them.
Possible limits: All selectable, one selectable, n# selectable

 */

public class AdvancedCheckbox extends CategoryItem implements InfoItem
{

    //String constants to store the type of this checkbox
    private final String single = "SINGLE";
    private final String multi = "MULTI";
    private final String all = "ALL";

    private int count = 3; // The amount of checkboxes the user can select in "Multi-select" mode.

    //List to store the options of checkboxes.
    private HashMap<String, Boolean> checkboxes;
    private String type = all;

    public AdvancedCheckbox(String name, Category category) {
        super(name, category);
        checkboxes = new HashMap<>();
    }

    public AdvancedCheckbox(String name, Category category, long ID) {
        super(name, category,ID);
        checkboxes = new HashMap<>();
    }

    //Mutators for checkbox names
    public void setCheckBoxNames(String... names)
    {

        for (String name : names)
            checkboxes.put(name, false);

    }
    public HashMap<String, Boolean> getCheckboxes()  { return this.checkboxes; }

    public void enableSingleSelect() { type = single; }
    public void enableMultiSelect() { type = multi; }
    public void enableAllSelect() { type = all; }

    public String getSelectionType() { return type; }

    public void setSelectionAmount(int count) { this.count = count; }
    public int getSelectionAmount() { return this.count; }

    private CompoundButton.OnCheckedChangeListener getSingleListener(ArrayList<CheckBox> others) {

        return (btn, checked) -> {
            if (checked)
            {

                //Disable the others
                for (CheckBox other : others)
                    other.setChecked(false);

            }
        };

    }

    private ArrayList<CompoundButton> selectedBoxes = new ArrayList<>(); // Store the checkboxes that are currently selected.
    private CompoundButton.OnCheckedChangeListener getMultiListener(String title)
    {

        return (btn, checked) -> {
          if (checked){

              if (this.count < selectedBoxes.size()) // If the max amount of boxes is less than the currently selected amount of boxes
                  selectedBoxes.get(0).setChecked(false); // Set the first selected btn to false. Removing it from the list.


              selectedBoxes.add(btn); // Add the btn to the end.
              this.count++;
              checkboxes.put(title, true); // Set the btn in the list to true.

          }
          else // This option was set to false
          {

              if (selectedBoxes.contains(btn))
                  selectedBoxes.remove(btn);

              checkboxes.put(title, false); // Set the btn in the list to false.

          }
        };

    }

    private CompoundButton.OnCheckedChangeListener getAllListener(String title)
    {

        return (btn, checked) -> {
            checkboxes.put(title, checked);
        };

    }

    public ArrayList<String> getCheckedBoxes()
    {

        ArrayList<String> checkedBoxes = new ArrayList<>();

        for (Map.Entry<String, Boolean> box : checkboxes.entrySet())
        {

            if (box.getValue())
                checkedBoxes.add(box.getKey());

        }

        return checkedBoxes;

    }

    public CompoundButton.OnCheckedChangeListener getListener(String buttonTitle, ArrayList<CheckBox> otherBoxes)
    {

        switch(type)
        {

            case all:
                return getAllListener(buttonTitle);
            case multi:
                return getMultiListener(buttonTitle);
            case single:
                return getSingleListener(otherBoxes);
            default:
                return null;

        }

    }

    @Override
    public Map<String, Object> save(InspectionData saveTo)
    {

        Map<String, Object> data = super.save(saveTo);

        data.put("Options", checkboxes);

        return data;

    }

    @Override
    public void loadFrom(Context context, Map<String, Object> itemData) {

        super.loadFrom(context, itemData);

        this.checkboxes = (HashMap<String, Boolean>) itemData.getOrDefault("Options", new HashMap<String, Boolean>());

        System.out.println("Loaded advanced checkbox from data: " + this.checkboxes);

    }
}
