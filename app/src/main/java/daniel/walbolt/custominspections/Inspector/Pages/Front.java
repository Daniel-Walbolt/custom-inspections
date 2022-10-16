package daniel.walbolt.custominspections.Inspector.Pages;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Constants.SystemTags;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Information;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.AdvancedCheckbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.PictureItem;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

/*

Every inspection has a Front System. This system is hard-coded to require basic information about the residence. This information is displayed uniquely onto the first page of the report.

This system gathers photos of the residence, weather of the inspection, and inspector opinions.

 */

public class Front extends System
{

    private ImageView frontImageView;
    private PictureItem frontImage;
    private AdvancedCheckbox weather;
    private Numeric temperature;
    private Information infoCategory;

    public Front()
    {
        super("Front", null);

        createDefaultCategories();

    }

    @Override
    protected void createDefaultCategories()
    {

        if (this.getCategories().isEmpty())
        {

            infoCategory = new Information(this);  // Create the category that stores our custom information

            frontImage = new PictureItem("Front Picture", infoCategory, true);
            infoCategory.addItem(frontImage);

            weather = new AdvancedCheckbox("Weather", infoCategory);
            weather.setCheckBoxNames("Sunny","Cloudy","Rain","Snow","Fog","Humid","Stormy","Hail");
            infoCategory.addItem(weather);

            temperature = new Numeric("Temperature", infoCategory);
            temperature.setVersion2(true);
            temperature.setUnit("\u00B0F");
            infoCategory.addItem(temperature);

            categories.add(infoCategory);

        }

    }

    @Override
    public ArrayList<SystemTags> getStatus()
    {

        ArrayList<SystemTags> statuses = new ArrayList<>();

        if (isComplete())
        {

            statuses.add(SystemTags.COMPLETE);

        }
        else
        {

            statuses.add(SystemTags.INCOMPLETE);

        }

        return statuses;

    }

    @Override
    public boolean isComplete()
    {

        //Return the status of this system.
        boolean image = frontImage.getCompletionStatus();
        boolean temp = !temperature.getText().isEmpty();
        boolean weather = this.weather.getCheckedBoxes().size() > 1;

        return image && temp && weather;

    }

    @Override
    public void open(Activity activity)
    {

        activity.setContentView(R.layout.front_page);

        LinearLayout page = activity.findViewById(R.id.system_page_container);
        page.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        for (Category category : categories)
            category.loadToPage(page);

    }

    @Override
    public InspectionData save()
    {

        //Create a Data object
        InspectionData systemInformation = new InspectionData(this);

        //Create a list to store THIS system's data.
        Map<String, Object> data = new HashMap<>();

        data.put("Temperature", temperature.getText());
        data.put("Weather", weather.save(systemInformation));
        data.put("Picture", frontImage.save(systemInformation));

        systemInformation.addSystemData(data);

        return systemInformation;

    }

    @Override
    public void loadFrom(Context context, Map<String, Object> allSystemData)
    {

        if (allSystemData.containsKey("System"))
        {

            Map<String, Object> systemData = (Map<String, Object>) allSystemData.get("System");

            java.lang.System.out.println("Data: " + systemData);
            temperature.setText((String) systemData.getOrDefault("Temperature", ""));
            if (systemData.containsKey("Weather"))
                weather.loadFrom(context, (Map<String, Object>) systemData.get("Weather"));
            frontImage.loadFrom(context, (Map<String, Object>) systemData.getOrDefault("Picture", new HashMap<String, Object>()));

        }

    }

    public InspectionMedia getFrontImage()
    {

        return frontImage.getMedia();

    }

    public String getTemp()
    {

        return temperature.getText();

    }

    public String getWeather()
    {

        StringBuilder weatherString = new StringBuilder();
        ArrayList<String> weatherOptions = weather.getCheckedBoxes();

        for (int i = 0 ; i < weatherOptions.size(); i ++)
        {

            weatherString.append(weatherOptions.get(i));

            if (i != weatherOptions.size() - 1)
                weatherString.append(", ");

        }

        return weatherString.toString();

    }
}