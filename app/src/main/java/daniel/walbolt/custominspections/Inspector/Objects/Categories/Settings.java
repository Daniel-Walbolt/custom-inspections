package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import daniel.walbolt.custominspections.Adapters.CategoryItemRecycler;
import daniel.walbolt.custominspections.Constants.SystemSetting;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ErrorAlert;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.SettingItem;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class Settings extends Category
{

    public Settings(System parent) {
        super(TYPE.SETTINGS, parent);
        createSettings();
    }

    @Override
    public void initRecycler()
    {

        CategoryItemRecycler adapter = new CategoryItemRecycler(categoryItems, emptyView, categoryRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(categoryRecycler.getContext(), RecyclerView.VERTICAL, false);
        categoryRecycler.setAdapter(adapter);
        categoryRecycler.setLayoutManager(manager);
        categoryRecycler.setNestedScrollingEnabled(false);

    }

    @Override
    public void loadToPage(LinearLayout pageLayout)
    {

        //Get the layout of the settings category
        View  categoryLayout = LayoutInflater.from(pageLayout.getContext()).inflate(R.layout.settings_category, pageLayout, false);

        //Initialize the settings recycler
        categoryRecycler = categoryLayout.findViewById(R.id.settings_recycler);
        initRecycler();

        //Add the category to the page
        pageLayout.addView(categoryLayout);


    }

    private void createSettings()
    {

        //Create the default settings for every system if they do not exist in that system.

        if(categoryItems.isEmpty())
        {

            SettingItem partial = new SettingItem("Mark System as Partially Complete", this, 1,(buttonView, isChecked)
                    -> getSystem().setSetting(SystemSetting.PARTIAL, isChecked));

            SettingItem complete = new SettingItem("Mark System as Complete", this, 2, null);
            complete.setCheckEvent((buttonView, isChecked) -> {
                getSystem().setSetting(SystemSetting.COMPLETE, isChecked);

                if(isChecked)
                {

                    //CHeck if the system has sub systems
                    if(!getSystem().getSubSystems().isEmpty())
                    {

                        for(System subSystem : getSystem().getSubSystems())
                        {

                            if(!subSystem.isComplete())
                            {

                                new ErrorAlert(buttonView.getContext(), "You can not complete a Main System until all Sub-Systems are complete!");
                                complete.setApplicability(false);
                                buttonView.setChecked(false);
                                break;

                            }

                        }

                    }

                }

            });

            SettingItem exclude = new SettingItem("Exclude System from Report", this, 3, (buttonView, isChecked)
                    -> {
                getSystem().setSetting(SystemSetting.EXCLUDED, isChecked);

                //If the system has subsystems, exclude them from the report as well.
                if(!getSystem().getSubSystems().isEmpty())
                {

                    for(System subSystem : getSystem().getSubSystems())
                    {

                        subSystem.setSetting(SystemSetting.EXCLUDED, isChecked);

                    }

                    getSystem().reloadCategory(TYPE.SUB_SYSTEMS);

                }

            });

            SettingItem quality = new SettingItem( "Mark as Quality of Residence", this, 4,(buttonView, isChecked)
                    -> getSystem().setSetting(SystemSetting.QUALITY, isChecked));

            categoryItems.addAll(Arrays.asList(complete, partial, quality, exclude));

        }
        
    }



}
