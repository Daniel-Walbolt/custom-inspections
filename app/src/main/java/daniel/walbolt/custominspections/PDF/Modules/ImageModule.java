package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.PDF.Module;
import daniel.walbolt.custominspections.R;

public class ImageModule extends Module
{

    /*
    The Image Module is purely for displaying an image. This will be utilized by all classes of information that use images with no comments.

    In order to maximize efficiency of page space, we will keep images and comments uncoupled. They can be displayed on adjacent pages if the space requires it.

     */

    private InspectionMedia image;

    public ImageModule(InspectionMedia image)
    {

        this.image = image;
        this.width = 162;
        this.height = 162;

    }

    @Override
    public void establishHeight()
    {

        //No loading functionality needed.

    }

    @Override
    public void establishWidth()
    {

        //No loading functionality needed.

    }

    @Override
    public View initAndGetViews(Context context)
    {

        View v = LayoutInflater.from(context).inflate(R.layout.module_image, null, false);

        ImageView imageView = v.findViewById(R.id.module_image_image);
        if (image.getURI(context) != null)
            imageView.setImageURI(image.getURI(context));

        return v;

    }

}
