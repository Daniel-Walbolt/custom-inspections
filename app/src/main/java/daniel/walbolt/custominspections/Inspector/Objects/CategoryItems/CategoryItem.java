package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;
import android.os.Build;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CommentDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MajorComponent;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Pages.Main;

public abstract class CategoryItem
{

    /*

    A CategoryItem is a type of user-interface used to gather input about a specific part of a residence.

    These sections can come in various forms:
    Generic list items - are true false options (checkboxes)
    Numeric items - expand when marked, and require a numeric input.
    Slider items - expand when marked, provide a slider for selecting one of many options


    Sections should be almost entirely customizable within working parameters of the app.
        - Pictures are an optional setting
        - Comments are an optional setting
        - Comment Hints
        - PDF Description
        - User Description

     */

    private Category category;
    private String name;
    private CategoryGroup group;
    private long ID; // ID number to identify this item across name changes.

    //Variables to store pictures & or comments
    private InspectionMedia commentMedia; // The comment media stores only a comment (in the case where no pictures are necessary)
    private ArrayList<InspectionMedia> pictures; // The picture media list stores pictures that can each have their own comments.

    // A condition that determines if the content for this item should be displayed.
    boolean isApplicable;

    //Variables to store customizable attributes
    private String commentHint;
    private String pdfDescription;
    protected String description; // String used to determine where this item is located. This is used by MajorComponents

    //To initialize a category item, you must have a category to put it in
    public CategoryItem(String name, Category category)
    {

        //Whenever an item is created, its ID is the system time
        this.ID = java.lang.System.currentTimeMillis();
        this.category = category;
        this.name = name;
        commentMedia = new InspectionMedia(this);

    }

    //Create a category item with a specific ID (this is called by the Configurator, and hard-coded CategoryItems)
    public CategoryItem(String name, Category category, long ID)
    {

        this.ID = ID;
        this.category = category;
        this.name = name;
        commentMedia = new InspectionMedia(this);

    }

    //This method is utilized by child-classes that can be MajorComponents. When they are deemed, major, this method adds those objects to a major component list.
    protected void addComponent(MajorComponent component)
    {

        Main.inspectionSchedule.inspection.addMajorComponent(component);

    }

    /*

    Getters and Setters

     */
    public String getName()
    {

        return name;

    }

    //Get the ID of this item. The ID is meant to never change. Every item has its own ID based off the time it was created.
    //The name of every item can change, so unique IDs are necessary for continuity between new and past inspections.
    public long getID()
    {

        return ID;

    }

    //This method should only be used by the configurator when loading an inspection.
    public void setID(long newID)
    {

        // -1L is the value passed when there is no saved ID for this item.
        if(newID != -1)
            this.ID = newID;

    }

    public String getCommentHint() {
        return commentHint;
    }

    public void setCommentHint(String commentHint) {
        this.commentHint = commentHint;
    }

    public String getPdfDescription() {
        return pdfDescription;
    }

    public void setPdfDescription(String pdfDescription) {
        this.pdfDescription = pdfDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //Delete this item from configuration and its group/category
    public void delete(Context context)
    {

        if(group != null)
            group.removeItem(context, this);
        else
            category.removeItem(context, this);

    }
    public CategoryGroup getGroup()
    {

        return group;

    }

    //This method is ONLY called by the CategoryItemDialog in EDIT mode.
    public void setName(String newName)
    {

        this.name = newName;

    }

    public void setGroup(CategoryGroup group)
    {

        this.group = group;

    }

    public Category getCategory()
    {

        return category;

    }

    public InspectionMedia getCommentMedia() // Get the comments for this item
    {

        if(commentMedia == null)
            commentMedia = new InspectionMedia(this);

        return commentMedia;

    }

    public int getMediaCount() // Return the number of pictures on this item (used by system media count)
    {

        if(pictures != null)
            return pictures.size();
        return 0;

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

    public boolean hasComments()
    {

        return !commentMedia.getComments().isEmpty();

    }

    public boolean isApplicable()
    {

        return isApplicable;

    }

    public void setApplicability(boolean applicability)
    {

        this.isApplicable = applicability;

    }

    public int getCommentCount() // Return the number of comments (in the comment media, NOT picture media comments)
    {

        return getCommentMedia().getComments().size();

    }

    /*

    Utility Methods

     */

    public void removeCommentMedia() // Remove the comments on this item
    {

        commentMedia = null;

    }

    public void openCommentDialog(Context context) // Open the dialog to edit the comments on this item
    {

        new CommentDialog(context, getCommentMedia());

    }

    public void addMedia(InspectionMedia picture)
    {

        if(pictures == null)
            pictures = new ArrayList<>();
        pictures.add(picture);

    }

    public boolean hasPictures()
    {

        return pictures != null && !pictures.isEmpty();

    }

    /*

    Save and Load methods for database integration

     */

    //This method should be overridden by sub-classes inorder to save its own information
    //THis method will save the basic information of EVERY object, including pictures and comments even IF they DON'T exist!
    // DO NOT SAVE COMMENTS OR PICTURES IN OTHER SUB-CLASSES
    public Map<String, Object> save(InspectionData saveTo) // Put information into an object that is easily saved to the database
    {

        //Key Value list to store basic information about all CategoryItems
        Map<String, Object> savedCheckable = new HashMap<>();

        //Save the name of this category item
        savedCheckable.put("Name", name);

        //Save the ID of this category item
        savedCheckable.put("ID", getID());

        //Save the comments in this item. IF there are no comments, an empty list is saved.
        savedCheckable.put("Comments", getCommentMedia().getComments());

        //Save the status of this item into the database. ALL items are saved, whether they are selected or not.
        savedCheckable.put("Applicable", isApplicable());

        //Create a list to store the data of images (an image has a name, and sometimes comments)
        ArrayList<Map<String, Object>> imageData = new ArrayList<>();

        //Save every media and add the data to the image-data list
        if(pictures != null)
            for(InspectionMedia media : getPictures())
            {
                //Save the data of the image
                imageData.add(media.save());

                //Store the image location, so that it can be uploaded.
                saveTo.addPicture(media);

            }
        //Now store the image data in the data for this CategoryItem. A nested HashMap.
        if(pictures != null && !pictures.isEmpty())
            savedCheckable.put("Pictures", imageData);

        return savedCheckable;

    }

    public void loadFrom(Context context, Map<String, Object> itemData) // Load information from a list converted from the database
    {

        if(itemData.containsKey("Applicable"))
            setApplicability((boolean) itemData.get("Applicable"));

        //Load comments and pictures
        if(itemData.containsKey("Comments"))
            getCommentMedia().addComments((ArrayList<String>)itemData.get("Comments"));
        if(itemData.containsKey("Pictures"))
        {

            for(Map<String, Object> image : (ArrayList<Map<String, Object>>) itemData.get("Pictures"))
            {

                InspectionMedia loadedPicture = new InspectionMedia(this).createImageFileFromDatabase(context, (String) image.get("Name"));
                if(image.containsKey("Comments"))
                    loadedPicture.addComments((ArrayList<String>) image.get("Comments"));
                getPictures().add(loadedPicture);

            }

        }

    }


}
