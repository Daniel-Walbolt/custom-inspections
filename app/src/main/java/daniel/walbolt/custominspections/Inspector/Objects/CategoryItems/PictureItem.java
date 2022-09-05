package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MajorComponent;
import daniel.walbolt.custominspections.Inspector.Pages.Main;

/*
PictureItem is predicted to only be used in programmed systems. These are not yet available to the user to add to Categories, but these items only take an image.

Picture Items require an input. You can not finish the report without giving an image to one of these items.
In the final report, these items are displayed as an image with or without a title. Because of their very specific application in the PDF (usually a specific spot on the page),
the user is unable to add these by themselves.

These items will still be initialized with an ID in the case that their title changes years from now. Loading an old inspection would mean losing the image that belonged to this class prior to the name change.

These items are automatically added to the major component list of every inspection.

 */

public class PictureItem extends CategoryItem implements MajorComponent
{

    private static final long ID = 123456;

    private boolean pictureTaken = false;

    public PictureItem(String name, Category category, boolean isMajor)
    {
        super(name, category, ID);

        //In order to utilize the CategoryItem's save functionality, we have to store this media in a list for pictures.
        //This also allows for the picture count functionality to work.
        getPictures().add(new InspectionMedia(this)); // Create the media to store this section's picture.

        if (isMajor)
            addComponent(this);

    }

    @Override
    public void setApplicability(boolean isApplicable)
    {

        this.isApplicable = true; // PictureItems are always applicable. They MUST have an input.

    }

    public InspectionMedia getMedia()
    {

        return getPictures().get(0);

    }

    public void setMedia(InspectionMedia media)
    {

        getPictures().set(0, media);

    }

    //This method should only be called after the CameraActivity closes with a result.
    public void setPictureTaken(boolean pictureStatus)
    {

        this.pictureTaken = pictureStatus;

    }

    @Override
    public boolean getCompletionStatus()
    {

        //Return true if there is an image saved in the file. Otherwise return false.
        return pictureTaken;

    }

    @Override
    public String getComponentDescription() {
        return "Residence Front Image";
    }
}
