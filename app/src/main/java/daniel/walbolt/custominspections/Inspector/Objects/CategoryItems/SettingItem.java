package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.view.View;
import android.widget.CompoundButton;

import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;

public class SettingItem extends CategoryItem
{

    private CompoundButton.OnCheckedChangeListener checkEvent;

    //Settings are custom checkbox items that perform a custom action when checked.
    //However, they are put onto the screen in the same way other category items are.
    // The event to perform is defined upon initialization and is called every time the CompoundButton is checked true.

    public SettingItem(String name, Category category, long settingID, CompoundButton.OnCheckedChangeListener checkEvent) {
        super(name, category, settingID);
        this.checkEvent = checkEvent;
    }

    public CompoundButton.OnCheckedChangeListener getCheckEvent()
    {

        return checkEvent;

    }

    public void setCheckEvent(CompoundButton.OnCheckedChangeListener event)
    {

        this.checkEvent = event;

    }

}
