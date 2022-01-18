package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;
import android.os.Build;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageCapture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CommentDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.System;

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
        - Comment Hints
        - PDF Description
        - User Description

     */

    private Category category;
    private String name;
    private CategoryGroup group;

    //Variables to store pictures & or comments
    private InspectionMedia commentMedia; // The comment media stores only a comment (in the case where no pictures are necessary)
    private ArrayList<InspectionMedia> pictures; // The picture media list stores pictures that can each have their own comments.

    // A condition that determines if the content for this item should be displayed.
    boolean isApplicable;

    //Variables to store customizable attributes
    private String commentHint;
    private String pdfDescription;
    private String description;

    //To initialize a category item, you must have a category to put it in
    public CategoryItem(String name, Category category)
    {

        this.category = category;
        this.name = name;
        commentMedia = new InspectionMedia(this);

    }

    /*

    Getters and Setters

     */
    public String getName()
    {

        return name;

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
    TODO

     */

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

                InspectionMedia loadedPicture = new InspectionMedia(this).createImageFileFromDatabase(context, (String) image.get("Name"));
                if(image.containsKey("Comments"))
                    loadedPicture.addComments((ArrayList<String>) image.get("Comments"));
                getPictures().add(loadedPicture);

            }

        }

    }


}
