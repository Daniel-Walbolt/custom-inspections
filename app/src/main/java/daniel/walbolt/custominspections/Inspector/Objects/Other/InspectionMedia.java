package daniel.walbolt.custominspections.Inspector.Objects.Other;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Objects.SystemSection;
import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;

public class InspectionMedia
{

    private File file;
    private ArrayList<String> comments;
    private Context mContext;
    private String fileName;
    private SystemSection mSystemSection;
    private boolean isCommentMedia = true;

    public InspectionMedia(SystemSection systemSection)
    {

        this.mSystemSection = systemSection;
        this.comments = new ArrayList<>();

    }


    public InspectionMedia createImageFile(Context context, String uniqueFileName)
    {

        this.mContext = context;
        File externalDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        this.fileName = uniqueFileName;
        this.file = new File(getDirs(externalDir), fileName.toUpperCase() + ".jpg");
        isCommentMedia = false;
        return this;

    }

    public InspectionMedia createImageFileFromDatabase(Context context, String uniqueFileName)
    {

        createImageFile(context, uniqueFileName);

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
            db.retrieveImage(this);

        }


        return this;

    }

    private File getDirs(File externalDir)
    {

        File mainDir = new File(externalDir, "RSInspections" + File.separator + "Images");
        mainDir.mkdirs();
        return mainDir;

    }

    public SystemSection getSystemSection()
    {

        return mSystemSection;

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

    public Uri getURI()
    {

        return FileProvider.getUriForFile(mContext, "roofandskillet.fileprovider", file);

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
