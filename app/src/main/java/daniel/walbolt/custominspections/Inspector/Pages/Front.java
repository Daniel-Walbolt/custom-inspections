package daniel.walbolt.custominspections.Inspector.Pages;

import java.util.ArrayList;
import java.util.Arrays;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Information;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Checkbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.PictureItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Slider;
import daniel.walbolt.custominspections.Inspector.Objects.System;

/*

Every inspection has a Front System. This system is hard-coded to require basic information about the residence. This information is displayed uniquely onto the first page of the report.

This system gathers photos of the residence, weather of the inspection, and inspector opinions.

 */

public class Front extends System
{

    private Category information;

    public Front()
    {
        super("Front", null);

        createItems();

    }

    @Override
    protected void createDefaultCategories()
    {

        if (this.categories.isEmpty())
        {

            // The default categories are different than a normal user-created System.
            information = new Information(this);
            this.categories.add(information);

        }

    }

    private void createItems()
    {

        PictureItem picture = new PictureItem("Front Picture", information, true);
        picture.setDescription("Picture showing the front of the residence; usually including the front-door side of the house, and front lawn.");
        information.addItem(picture); // Add the picture item to the category

        CategoryGroup weather = new CategoryGroup("Weather",  information);
        Checkbox sunny = new Checkbox("Sunny", information); weather.addItem(sunny);
        Checkbox cloudy = new Checkbox("Cloudy", information);  weather.addItem(cloudy);
        Checkbox light_rain = new Checkbox("Light Rain", information); weather.addItem(light_rain);
        Checkbox heavy_rain = new Checkbox("Heavy Rain", information); weather.addItem(heavy_rain);
        Checkbox snow = new Checkbox("Snow / Ice", information); weather.addItem(snow);
        Numeric temperature = new Numeric("Temperature", information);
            temperature.setUnit("Fahrenheit");
            weather.addItem(temperature);

        information.addItem(weather);


    }


}
