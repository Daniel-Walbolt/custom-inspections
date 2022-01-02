package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class Media extends Category
{

    //The media category handles media display, taking, and saving of pictures.

    private ArrayList<InspectionMedia> media;    //List object for pictures
    private String title; // Title of the category

    public Media(String title, System parentSystem)
    {
        super(Category.TYPE.MEDIA, parentSystem);

        //Create a list for pictures to be stored
        media = new ArrayList<>();

        //Set the title of the category
        this.title = title;

    }

    public void takePhoto()
    {

        // TODO: Open camera using CameraX

    }

    public void savePhoto()
    {



    }

    private void loadMedia()
    {

        // TODO: Add photos to list

    }

    public void load(LinearLayout parent) //Load the media category, inflating views and adding itself to the parent view
    {

        /*View media_category  = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_category, parent, false);

        //TODO: Find and initialize the category's internal views
        TextView emptyView = media_category.findViewById(R.id.media_category_emptyView);
        TextView title = media_category.findViewById(R.id.media_category_title);
        title.setText(this.title);

        Button addPicture = media_category.findViewById(R.id.media_category_emptyView)


        parent.addView(media_category);*/

    }

}
