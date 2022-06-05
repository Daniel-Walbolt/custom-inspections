package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.CategoryItemRecycler;
import daniel.walbolt.custominspections.Adapters.MediaRecyclerAdapter;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MediaRecycler;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class Media extends Category
{

    //The media category handles media display, taking, and saving of pictures.

    private ArrayList<InspectionMedia> media;    //List object for pictures

    public Media(System parentSystem)
    {
        super(Category.TYPE.MEDIA, parentSystem);

        //Create a list for pictures to be stored
        media = new ArrayList<>();

    }

    public void takePhoto()
    {



    }

    private void loadMedia()
    {



    }

    @Override
    public void initRecycler()
    {

        //The information category loads basic CategoryItems (Check Boxes, Sliders, Numerics, Groups)
        //The CategoryItemRecycler handles groups as well as other items
        MediaRecyclerAdapter adapter = new MediaRecyclerAdapter(media, categoryRecycler, emptyView);
        LinearLayoutManager manager = new LinearLayoutManager(categoryRecycler.getContext(), RecyclerView.HORIZONTAL, false);
        categoryRecycler.setAdapter(adapter);
        categoryRecycler.setLayoutManager(manager);
        categoryRecycler.setNestedScrollingEnabled(false);

    }

    public void loadToPage(LinearLayout parent) //Load the media category, inflating views and adding itself to the parent view
    {

        LinearLayout media_category  = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.media_category, parent, false);

        //TODO: Find and initialize the category's internal views
        categoryRecycler =  media_category.findViewById(R.id.media_category_recycler);
        emptyView = media_category.findViewById(R.id.media_category_emptyView);
        ImageButton addPicture = media_category.findViewById(R.id.media_category_add);
        addPicture.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                //Create a media object to store the reference to the image and handle saving and loading it
                InspectionMedia imageController = new InspectionMedia((Media) thisClass());

                //Add the media object to this category's media
                media.add(imageController);

                imageController.takePicture(view.getContext());

            }

        });


        initRecycler();

        parent.addView(media_category);

    }

}
