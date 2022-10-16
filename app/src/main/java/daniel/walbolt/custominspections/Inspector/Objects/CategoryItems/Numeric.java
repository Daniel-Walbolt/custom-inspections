package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;

public class Numeric extends CategoryItem implements InfoItem
{

    private String text = ""; // While the entry type is always a number, it's stored as a string to avoid removing zero's in the suffix or prefix.
    private String unit = "Units"; // The unit that defines what the number is.
    private boolean isVersion2 = false; // Boolean that decides how to render this item.

    public Numeric(String name, Category category)
    {
        super(name, category);

    }

    public Numeric(String name, Category category, long ID) {
        super(name, category, ID);
    }

    public void setText(String numericText)
    {

        this.text = numericText;

    }

    //This method is a setter whether or not this numeric should be rendered using the 2nd layout of a numeric.
    public void setVersion2(boolean version2)
    {

       this.isVersion2 = version2;

    }

    public boolean isVersion2()
    {

        return this.isVersion2;

    }

    public String getText()
    {

        return text;

    }

    public void setUnit(String unit)
    {

        this.unit = unit;

    }

    public String getUnit()
    {

        return unit;

    }

    @Override
    public Map<String, Object> save(InspectionData saveTo)
    {

        Map<String, Object> itemData = super.save(saveTo);

        itemData.put("Value", text);

        return itemData;

    }

    @Override
    public void loadFrom(Context context, Map<String, Object> itemData)
    {

        super.loadFrom(context, itemData);

        if  (itemData.containsKey("Value"))
            text = (String) itemData.get("Value");

    }

}
