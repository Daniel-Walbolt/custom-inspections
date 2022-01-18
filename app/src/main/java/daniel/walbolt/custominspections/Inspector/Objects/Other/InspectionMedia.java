package daniel.walbolt.custominspections.Inspector.Objects.Other;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Activities.CameraActivity;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;

public class InspectionMedia
{

    private File file;
    private ArrayList<String> comments;
    private String fileName;
    private CategoryItem section;
    private boolean isCommentMedia = true; // By default, all InspectionMedia objects are for comments only. until an image is added to them.

    /*

    The InspectionMedia object handles everything related to pictures.

    While various things may trigger the taking of a picture, this is the class that handles the action.

    This class also loads and saves pictures to the device and database.

     */
    public InspectionMedia(CategoryItem section)
    {

        this.section = section;
        this.comments = new ArrayList<>();

    }


    //This method creates the file where the image will be stored
    public void createImageFile(Context context)
    {

        //Create the placeholder for the image that will be created by the camera activity.
        // OR the file location for the image being loaded from the database.
        this.file = new File(getDirectories(context), fileName + ".jpg");

        //Create the directories that don't exist
        this.file.mkdirs();

        //This media is no longer a comment media only.
        isCommentMedia = false;

    }

    public InspectionMedia createImageFileFromDatabase(Context context, String uniqueFileName)
    {

        //Designate a file location for the image being loaded from the database
        this.fileName = uniqueFileName;
        createImageFile(context);

        boolean createdImageFile = false;
        try
        {

            createdImageFile = file.createNewFile();

        }
        catch (IOException e)
        {

            e.printStackTrace();

        }

        if(createdImageFile)
        {

            FirebaseBusiness db = FirebaseBusiness.getInstance();
            db.retrieveImage(context, this );

        }


        return this;

    }

    //This method is called when the picture icon is clicked on.
    public void takePicture(Context context)
    {

        //Create the file the directories that will store the image.
        // The image can be cancelled, but the use of storage here is minimal.
        this.fileName = section.getSystem().getDisplayName() + File.separator + section.getName() + "_" + System.currentTimeMillis();
        createImageFile(context);

        //Create an activity intent to open the custom camera
        Intent cameraIntent = new Intent(context, CameraActivity.class);

        // Pass the file created by the media object to the camera
        cameraIntent.putExtra("File", this.file);
        context.startActivity(cameraIntent);

    }

    //Return the directory where all images should be saved
    private File getDirectories(Context context)
    {

        //Get the App's personal file directory
        File externalDir = context.getFilesDir();

        //Create / get this Inspection's directory based on its ID.
        File mainDir = new File(externalDir,Main.inspectionSchedule.getScheduleID());

        return mainDir;

    }

    public CategoryItem getSystemSection()
    {

        return section;

    }

    public boolean isCommentMedia()
    {

        return isCommentMedia;

    }

    public File getFile()
    {

        return file;

    }

    public ArrayList<String> getComments()
    {

        return comments;

    }

    public String getFileName()
    {

        return fileName;

    }

    public void addComments(String... comments)
    {

        this.comments.addAll(Arrays.asList(comments));

    }

    public void addComments(ArrayList<String> comments)
    {

        this.comments.addAll(comments);

    }

    public Uri getURI(Context context)
    {

        if(file == null)
            return null;
        else
            return Uri.fromFile(file);

    }

    public void CompressImageFile()
    {

        /*
        TODO: Compress images using CameraX
         */

    }

    public Map<String, Object> save()
    {

        Map<String, Object> data = new HashMap<>();
        if(file != null) {
            data.put("Name", fileName);
        }
        if(comments != null && !comments.isEmpty())
            data.put("Comments", comments);

        return data;

    }

}
