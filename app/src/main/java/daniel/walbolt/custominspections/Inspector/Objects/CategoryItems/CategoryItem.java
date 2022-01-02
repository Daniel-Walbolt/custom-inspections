package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CommentDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Objects.SystemSection;

public abstract class CategoryItem
{

    /*

    A CategoryItem is a type of user-interface used to gather input about a specific part of a residence.

    These sections can come in various forms:
    Generic list items - are true false options (checkboxes)
    Numeric items - expand when marked, and require a numeric input.
    Slider items - expand when marked, provide a slider for input within parameters (numeric, or named steps: low med high)


    Sections should be almost entirely customizable within working parameters of the app.
        - Pictures are an optional setting
        - Comments are an optional setting

     */

    private Category category;
    private String name;
    private SystemSection section;
    private CategoryGroup group;

    //Variables to store pictures & or comments
    private InspectionMedia commentMedia; // The comment media stores only a comment (in the case where no pictures are necessary)
    private ArrayList<InspectionMedia> pictures; // The picture media list stores pictures that can each have their own comments.

    // Is Applicable is a condition that determines if the VIEW for this item should be displayed. I.e. True, this residence has a water heater and the age should be determined, false it does not.
    private boolean isApplicable;

    private boolean hasComments = false;
    private boolean autoOpenComments = false; // Auto open comments is an optional feature for every category item that automatically opens the comments when the item is marked as APPLICABLE.

    //To initialize a category item, you must have a category to put it in
    public CategoryItem(String name, Category category)
    {

        this.category = category;
        this.name = name;

        section = new SystemSection(this);

    }

    public String getName()
    {

        return name;

    }

    //This method is ONLY called by the CategoryItemDialog in EDIT mode.
    public void setName(String newName)
    {

        this.name = newName;

    }

    public CategoryGroup getGroup()
    {

        return group;

    }

    public void setGroup(CategoryGroup group)
    {

        this.group = group;

    }

    public Category getCategory()
    {

        return category;

    }

    public abstract void onChecked(); // What to do when this item is checked TRUE


    public InspectionMedia getCommentMedia() // Get the comments for this item
    {

        if(commentMedia == null)
            commentMedia = new InspectionMedia(section);

        return commentMedia;

    }

    public System getSystem() // Get the system this item belongs to
    {

        return category.getSystem();

    }

    public ArrayList<InspectionMedia> getPictures() // Retrieve the picture medias
    {

        //If there are no pictures, return an empty ArrayList.
        if(pictures == null)
            pictures = new ArrayList<>();

        return pictures;

    }

    public abstract void load(LinearLayout categoryLayout); // Method used to load this category item into the Category layout

    public int getMediaCount() // Return the number of pictures on this item (used by system media count)
    {

        if(pictures != null)
            return pictures.size();
        return 0;

    }

    public void removeCommentMedia() // Remove the comments on this item
    {

        commentMedia = null;

    }

    public void setHasComments(boolean hasComments, boolean autoOpenComments)
    {

        this.hasComments = hasComments;
        this.autoOpenComments = autoOpenComments;

    }

    public boolean hasAutoOpenComments()
    {

        return autoOpenComments;

    }

    public boolean hasComments()
    {

        return hasComments;

    }

    public boolean isApplicable()
    {

        return isApplicable;

    }

    public void setApplicability(boolean applicability)
    {

        this.isApplicable = applicability;

    }

    public void openCommentDialog(Context context) // Open the dialog to edit the comments on this item
    {

        new CommentDialog(context, getCommentMedia());

    }

    public void addPicture(InspectionMedia picture)
    {

        if(pictures == null)
            pictures = new ArrayList<>();
        pictures.add(picture);

    }

    public boolean hasPictures()
    {

        return pictures != null && !pictures.isEmpty();

    }

    public int getCommentCount() // Return the number of comments (in the comment media, NOT picture media comments)
    {

        return getCommentMedia().getComments().size();

    }

    /*public Map<String, Object> save(InspectionData savingTo) // Put information into an object that is easily saved to the database
    {

        Map<String, Object> savedCheckable = new HashMap<>();

        savedCheckable.put("Name", name);
        if(commentMedia != null)
            savedCheckable.put("Comments", commentMedia.getComments());

        if(pictures != null && !pictures.isEmpty())
            savedCheckable.put("Pictures", getSystem().saveImageMedia(getPictures(), savingTo));

        return savedCheckable;

    }*/

    public void loadFrom(Context context, Map<String, Object> inspectorCheckableData) // Load information from a list converted from the database
    {

        setApplicability(true);

        if(inspectorCheckableData.containsKey("Comments"))
            getCommentMedia().addComments((ArrayList<String>)inspectorCheckableData.get("Comments"));
        if(inspectorCheckableData.containsKey("Pictures"))
        {

            for(Map<String, Object> image : (ArrayList<Map<String, Object>>) inspectorCheckableData.get("Pictures"))
            {

                InspectionMedia loadedPicture = new InspectionMedia(this.section).createImageFileFromDatabase(context, (String) image.get("Name"));
                if(image.containsKey("Comments"))
                    loadedPicture.addComments((ArrayList<String>) image.get("Comments"));
                getPictures().add(loadedPicture);

            }

        }

    }


}
