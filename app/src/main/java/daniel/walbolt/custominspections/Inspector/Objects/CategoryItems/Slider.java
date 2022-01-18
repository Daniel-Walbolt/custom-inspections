package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;

public class Slider extends CategoryItem
{

    //The slider is a basic info-item with an easy slider to pick amongst several values.
    // These values are determined when creating the item, and can be edited.

    private ArrayList<String> content; // This list contains the words that should be displayed at each section

    public Slider(String name, Category category) {
        super(name, category);
        content = new ArrayList<>();
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

    public float getMin()
    {

        return 1; // The minimum is currently ALWAYS 1. The recycler that uses this value starts at index # 1

    }


}
