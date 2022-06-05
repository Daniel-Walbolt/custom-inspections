package daniel.walbolt.custominspections.Inspector.Objects.Other;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Activities.CameraActivity;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Media;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;

public class InspectionMedia
{

    private File imageFile;
    private ArrayList<String> comments;
    private String fileName;
    private CategoryItem section;
    private Media category;
    private boolean isCommentMedia = true; // By default, all InspectionMedia objects are for comments only. until an image is added to them.

    /*

    The InspectionMedia object handles everything related to pictures.

    While various things may trigger the taking of a picture, this is the class that handles the action.

    This class also loads and saves pictures to the device and database.

     */
    public InspectionMedia(CategoryItem section)
    {

        this.section = section;
        this.category = null;
        this.comments = new ArrayList<>();

    }

    //Media category can also store pictures directly.
    public InspectionMedia(Media category)
    {

        //Media Category pictures do not have comments.
        this.section = null;
        this.comments = null;
        this.category = category;

    }


    //This method creates the file where the image will be stored
    public void createImageFile(Context context)
    {

        //Create the placeholder for the image that will be created by the camera activity.
        // OR the file location for the image being loaded from the database.
        this.imageFile = new File(getDirectories(context), fileName + ".jpg");

        //Create the directories that don't exist
        this.imageFile.mkdirs();

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

            createdImageFile = imageFile.createNewFile();

        }
        catch (IOException e)
        {

            Toast.makeText(context, "Error creating image file", Toast.LENGTH_SHORT);

        }

        if(createdImageFile)
        {

            FirebaseBusiness db = new FirebaseBusiness();
            db.retrieveImage(context, this );

        }


        return this;

    }

    //This method is called when the picture icon is clicked on.
    public void takePicture(Context context)
    {

        //Create the file the directories that will store the image.
        // The image can be cancelled, but the use of storage here is minimal.
        if(section != null)
            this.fileName = getSectionMediaFileName(section);
        else
            this.fileName = getContextMediaFileName(category);

        //Create the directories and the file that will store the image
        createImageFile(context);

        //Create an activity intent to open the custom camera
        Intent cameraIntent = new Intent(context, CameraActivity.class);

        // Pass the file created by the media object to the camera
        cameraIntent.putExtra("File", this.imageFile);
        context.startActivity(cameraIntent);

    }

    //Return the directory where all images should be saved
    private static File getDirectories(Context context)
    {

        //Get the App's personal file directory
        File externalDir = context.getFilesDir();

        //Create / get this Inspection's directory based on its ID.
        File mainDir = new File(externalDir,"Inspection Pictures");
        mainDir.mkdirs();

        return mainDir;

    }

    public static void deleteDirectories(Context context)
    {

        //Delete the saved pictures from the inspection.
        //This operation should only be done at the beginning of each inspection.

        File pictureDirectory = getDirectories(context);

        pictureDirectory.delete();

    }

    public CategoryItem getSystemSection()
    {

        return section;

    }

    public boolean isCommentMedia()
    {

        return isCommentMedia;

    }

    public File getImageFile()
    {

        return imageFile;

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

        if(imageFile == null)
            return null;
        else
            return Uri.fromFile(imageFile);

    }

    public void CompressImageFile()
    {

        /*
        TODO: Compress images using CameraX
         */

    }

    public static String getContextMediaFileName(Media mediaCategory)
    {

        //Electrical\CONTEXT_MEDIA\IMAGE_4028194201894
        return mediaCategory.getSystem().getDisplayName() + File.separator + "CONTEXT_MEDIA" + File.separator + "IMAGE_" + System.currentTimeMillis();

    }

    public static String getSectionMediaFileName(CategoryItem section)
    {

        //Electrical\Information\Looped_Wire\Image_4271948271947281

        return section.getSystem().getDisplayName() + File.separator  + section.getCategory().getName()
                + File.separator + section.getName() + File.separator  + "IMAGE_" + System.currentTimeMillis();

    }

    public Map<String, Object> save()
    {

        Map<String, Object> data = new HashMap<>();
        if(imageFile != null) {
            data.put("Name", fileName);
        }
        if(comments != null && !comments.isEmpty())
            data.put("Comments", comments);

        return data;

    }

}
