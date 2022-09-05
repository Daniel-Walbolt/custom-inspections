package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Checkbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.InfoItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Slider;
import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.R;

 /*
    The Checklist Module displays Information class items. Sliders, numerics, checkboxes.
    This module only displays 1 section, or group of items, at a time.
 */

public class ChecklistModule extends Module {

    private ArrayList<CategoryItem> information;
    private String header;

    public ChecklistModule(String sectionTitle, ArrayList<CategoryItem> items)
    {

        this.header = sectionTitle;
        this.information = new ArrayList<>();
        this.information.addAll(items);

    }

    @Override
    public void establishHeight()
    {

        //The height is determined by the amount of CategoryItems being displayed.
        int totalHeight = 60;

        for (CategoryItem item : information)
        {

            totalHeight += 50; // Each item is displayed by a 50px tall TextView

        }

        this.height = totalHeight;

    }

    @Override
    public void establishWidth()
    {

        //This module has a defined max width to maximize horizontal content and flexible names of sections.
        this.width = 400;

    }

    @Override
    public View initAndGetViews(Context context)
    {

        View v = LayoutInflater.from(context).inflate(R.layout.module_checklist, null, false);

        TextView header = v.findViewById(R.id.module_checklist_header);
            header.setText(this.header);

        LinearLayout container = v.findViewById(R.id.module_checklist_container); // The container might just be View v, as well.
            for (CategoryItem item : information)
            {

                View itemView = LayoutInflater.from(context).inflate(R.layout.module_checklist_info, container, false);

                TextView itemTitle = itemView.findViewById(R.id.module_info_title);
                    itemTitle.setText(item.getName());

                TextView itemOption = itemView.findViewById(R.id.module_info_option);
                    if (item instanceof Checkbox)
                        itemOption.setVisibility(View.GONE);
                    else
                    {

                        if (item instanceof Slider)
                            itemOption.setText(" - " + ((Slider)item).getSelectedItem());
                        else  // Item is a numeric
                            itemOption.setText(" - " +  ((Numeric)item).getText() + " " + ((Numeric)item).getUnit());

                    }
                    
                container.addView(itemView);

            }

        return v;
    }


}
