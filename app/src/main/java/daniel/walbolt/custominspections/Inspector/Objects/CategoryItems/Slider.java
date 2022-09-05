package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;

public class Slider extends CategoryItem implements InfoItem
{

    //The slider is a basic info-item with an easy slider to pick amongst several values.
    // These values are determined when creating the item, and can be edited.

    private ArrayList<String> content; // This list contains the words that should be displayed at each section
    private int progress = 1; // Default progress is the minimum

    public Slider(String name, Category category) {
        super(name, category);
        content = new ArrayList<>();
    }

    public Slider(String name, Category category, long ID) {
        super(name, category, ID);
    }

    public void setContent(ArrayList<String> content)
    {

        this.content = content;

    }

    //This method is used by the CategoryItem EDIT dialog, and by any recycler that uses a Slider item.
    public ArrayList<String> getContent()
    {

        return this.content;

    }

    //This method is used by any recycler displaying this item
    public int getMax()
    {

        if(content.size() >= 2)
            return content.size();
        return 2; // THe minimum value of a slider. All the way to the left or right.

    }

    public void setProgress(int newProgress)
    {
        this.progress = newProgress;
    }

    public int getProgress()
    {

        return this.progress;

    }

    public float getMin()
    {

        return 1; // The minimum is currently ALWAYS 1. The recycler that uses this value starts at index # 1

    }

    public String getSelectedItem()  // Currently only called by PDF Modules. Returns the string that matches the progress, defaults if the slider doesn't have options
    {

        if (content.size() > progress-1)
            return content.get(progress - 1);
        else
            return "Default Option";

    }

    @Override
    public Map<String, Object> save(InspectionData saveTo)
    {

        Map<String, Object> itemData = super.save(saveTo);

        itemData.put("Progress", progress);

        return itemData;

    }

    @Override
    public void loadFrom(Context context, Map<String, Object> itemData)
    {

        super.loadFrom(context, itemData);

        if (itemData.containsKey("Progress")) {
            Long progressLong = (long) itemData.get("Progress");
            progress = progressLong.intValue();

        }

    }

}
