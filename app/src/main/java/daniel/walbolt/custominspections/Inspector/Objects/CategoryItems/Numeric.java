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
