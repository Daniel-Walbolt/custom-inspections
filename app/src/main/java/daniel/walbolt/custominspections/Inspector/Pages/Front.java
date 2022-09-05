package daniel.walbolt.custominspections.Inspector.Pages;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Constants.SystemTags;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Information;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Media;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Checkbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.PictureItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Slider;
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
    private boolean sunny, cloudy, stormy, rain, snow, hail, dry, humid, fog = false;
    private Media mediaCategory;

    private String temperatureText = "";

    public Front()
    {
        super("Front", null);

        createItems();

    }

    @Override
    protected void createDefaultCategories()
    {

        if (this.getCategories().isEmpty())
        {

            mediaCategory = new Media(this);
            categories.add(mediaCategory);

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
        boolean temp = !temperatureText.isEmpty(); // Edit Text can only contain up to 3 numbers, no text
        boolean weather = weatherChecked();

        return image && temp && weather;

    }

    @Override
    public void open(Activity activity)
    {

        activity.setContentView(R.layout.front_page);

        //Initialize the views
        ImageButton imageButton = activity.findViewById(R.id.front_page_capture_image);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Create a media object to store the reference to the image and handle saving and loading it
                InspectionMedia imageController = new InspectionMedia(mediaCategory);

                //Add the media object to this category's media
                frontImage.setMedia(imageController);

                imageController.takePicture(view.getContext());

            }

        });

        frontImageView = activity.findViewById(R.id.front_page_front_image);
        if (frontImage.getMedia().getImageFile() != null && frontImage.getMedia().getImageFile().exists()) {

            frontImage.setPictureTaken(true);
            frontImageView.setImageURI(frontImage.getMedia().getURI(activity)); // Display the image

        }

        /*
        The inspector is required to describe the weather during inspection.
        The more detail the better, generally. Only of the options is required... more are possible.
         */
        CheckBox sunnyCheck = activity.findViewById(R.id.front_page_weather_sunny); sunnyCheck.setChecked(sunny);
        CheckBox cloudyCheck = activity.findViewById(R.id.front_page_weather_cloudy); cloudyCheck.setChecked(cloudy);
        CheckBox rainCheck = activity.findViewById(R.id.front_page_weather_rain); rainCheck.setChecked(rain);
        CheckBox stormyCheck = activity.findViewById(R.id.front_page_weather_stormy); stormyCheck.setChecked(stormy);
        CheckBox snowCheck = activity.findViewById(R.id.front_page_weather_snow); snowCheck.setChecked(snow);
        CheckBox dryCheck = activity.findViewById(R.id.front_page_weather_dry); dryCheck.setChecked(dry);
        CheckBox hailCheck = activity.findViewById(R.id.front_page_weather_hail); hailCheck.setChecked(hail);
        CheckBox humidCheck = activity.findViewById(R.id.front_page_weather_humid); humidCheck.setChecked(humid);
        CheckBox fogCheck = activity.findViewById(R.id.front_page_weather_fog); fogCheck.setChecked(fog);

        sunnyCheck.setOnCheckedChangeListener((btn, b) -> {
           if (b)
           {

               cloudyCheck.setChecked(false);
               stormyCheck.setChecked(false);

           }

           sunny = b;

        });

        cloudyCheck.setOnCheckedChangeListener((btn, b) -> {
            if (b)
            {
                sunnyCheck.setChecked(false);
                stormyCheck.setChecked(false);
            }
            cloudy = b;
        });

        stormyCheck.setOnCheckedChangeListener((btn, b) -> {
            if (b)
            {
                sunnyCheck.setChecked(false);
                cloudyCheck.setChecked(false);
            }
            stormy = b;
        });

        rainCheck.setOnCheckedChangeListener((btn, b) -> {
            rain = b;
        });

        snowCheck.setOnCheckedChangeListener((btn, b) -> {
            snow = b;
        });

        dryCheck.setOnCheckedChangeListener((btn, b) -> {
            dry = b;
        });

        hailCheck.setOnCheckedChangeListener((btn, b) -> {
            hail = b;
        });

        humidCheck.setOnCheckedChangeListener((btn, b) -> {
            humid = b;
        });

        fogCheck.setOnCheckedChangeListener((btn, b) -> {
            fog = b;
        });

        EditText temperature = activity.findViewById(R.id.front_page_temp);
            temperature.setText(temperatureText);
            temperature.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    temperatureText = s.toString();
                }
            });

    }

    @Override
    public InspectionData save()
    {

        //Create a Data object
        InspectionData systemInformation = new InspectionData(this);

        //Create a list to store THIS system's data.
        Map<String, Object> data = new HashMap<>();

        data.put("Temperature", temperatureText);
        data.put("Weather", saveWeather());
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

            temperatureText = (String) systemData.getOrDefault("Temperature", "");
            loadWeather((ArrayList<String>)systemData.getOrDefault("Weather", new ArrayList<String>()));
            frontImage.loadFrom(context, (Map<String, Object>) systemData.getOrDefault("Picture", new HashMap<String, Object>()));

        }

    }

    public InspectionMedia getFrontImage()
    {

        return frontImage.getMedia();

    }

    private void createItems()
    {

        frontImage = new PictureItem("Front Image", mediaCategory, true);
        mediaCategory.addItem(frontImage);

    }

    // Return the condition of the weather items. As of right now, only 1 of the main weathers is required to be a valid selection.
    private boolean weatherChecked()
    {

        return sunny || stormy || cloudy;

    }

    public String getTemp()
    {

        return temperatureText;

    }

    public String getWeather()
    {

        StringBuilder weather = new StringBuilder();

        //One of these is required, so it comes first
        if (sunny)
            weather.append("Sunny");
        else if(cloudy)
            weather.append("Cloudy");
        else if(stormy)
            weather.append("Stormy");

        //The next weather options are to further describe the above options.
        if (rain)
            weather.append(", Rain");
        if (snow)
            weather.append(", Snow");
        if (hail)
            weather.append(", Hail");
        if (dry)
            weather.append(", Dry");
        if (humid)
            weather.append(", Humid");
        if (fog)
            weather.append(", Fog");

        return weather.toString();

    }

    //Save the types of the weather that are applicable
    private ArrayList<String> saveWeather()
    {

        ArrayList<String> weather = new ArrayList<>();

        if (sunny)
            weather.add("Sunny");
        if(stormy)
            weather.add("Stormy");
        if (cloudy)
            weather.add("Cloudy");
        if (rain)
            weather.add("Rain");
        if (snow)
            weather.add("Snow");
        if (hail)
            weather.add("Hail");
        if (dry)
            weather.add("Dry");
        if(humid)
            weather.add("Humid");
        if (fog)
            weather.add("Fog");

        return weather;

    }

    //Load the weathers based on the previous method's output.
    private void loadWeather(ArrayList<String> savedWeather)
    {

        for (String weather : savedWeather)
        {

            if (weather.equals("Sunny"))
                sunny = true;
            else if (weather.equals("Stormy"))
                stormy = true;
            else if (weather.equals("Cloudy"))
                cloudy = true;
            else if (weather.equals("Rain"))
                rain = true;
            else if (weather.equals("Snow"))
                snow = true;
            else if (weather.equals("Hail"))
                hail = true;
            else if (weather.equals("Dry"))
                dry = true;
            else if (weather.equals("Humid"))
                humid = true;
            else if (weather.equals("Fog"))
                fog = true;

        }

    }

}
