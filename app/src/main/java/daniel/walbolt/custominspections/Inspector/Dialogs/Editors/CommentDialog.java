package daniel.walbolt.custominspections.Inspector.Dialogs.Editors;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.CommentList.CommentListItemTouchHelper;
import daniel.walbolt.custominspections.Adapters.CommentList.CommentListRecyclerAdapter;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ErrorAlert;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.DescriptionDialog;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.R;

public class CommentDialog extends Dialog
{

    private CategoryItem section;

    /*
    View Variables
     */
    private Button edit; // Navigation option
    private Button add; // Navigation option

    private InspectionMedia media; // The media that is being edited by this comment dialog.

    private EditText newComment; // Editable Text field for the new comment

    private TextView editHint;

    private Button commit; // Save the comment to the media / to the report.
    private Button save; // Save the comment to "Saved Comments"

    /*
    Saved Comments Variables
     */
    private TextView globalScope; //Button for accessing global scope
    private TextView systemScope; //Button for accessing system scope
    private TextView sectionScope; //Button for accessing section scope

    private ArrayList<String> currentSavedComments; // This is the list that contains the currently loaded saved comments.

    private File savedCommentsFile;

    private RecyclerView reportCommentsList; // This item displays the comments that are saved to the report (not necessarily the device)
    private RecyclerView savedCommentsList; // This item displays the currently loaded saved comments.

    /*
    Dialog State Variables
     */
    private String editedComment;
    private ArrayList<String> targetSavedComments;
    private File targetCommentsFile;


    //State booleans
    boolean isSectionScope;
    public boolean isEditing = false;
    public boolean isAdding = false;
    private boolean isEditingSavedComment = false;

    File commentDirectory = new File(getContext().getFilesDir() + File.separator + "Comments");
    File systemCommentDirectory = new File(commentDirectory + File.separator + "Systems");
    File sectionCommentDirectory = new File(commentDirectory + File.separator + "Sections");

    public CommentDialog(Context context, InspectionMedia media)
    {

        super(context);
        setContentView(R.layout.comment_dialog);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.section = media.getSystemSection();
        this.media = media;
        systemCommentDirectory.mkdirs();
        sectionCommentDirectory.mkdirs();

        setSectionScope(); // The section scope is the default place to look for comments.
        openCommentEditorHome(); // Initialize the home page views
        show(); // Show the dialog to the user

        //If the section has a hint (what they should say in their comments) for the user, show it.
        if(section.getCommentHint() != null)
        {

            new DescriptionDialog(context, section.getCommentHint());

        }

    }

    /*
    Navigation Methods
     */
    private void openCommentEditorHome() // Show and initialize the "home" views
    {

        setContentView(R.layout.comment_dialog);
        isEditing = false;
        isAdding = false;
        initMainNavButtons(); // The add and edit buttons
        initMediaCommentsRecycler(); // This recycler shows the comments currently in the media object

        //Set the saved comments variables
        setSectionScope();
        currentSavedComments = getSavedCommentsList();

        //Initialize saved comments functionality
        initSavedCommentsRecycler();
        initScopeButtons();

        setVisibilities(View.GONE); // Hide the views associated with the EDIT state.

    }

    //This method is called by the ADD button in the "home" page
    private void openCommentEditorAdd()
    {

        //Set the ADD comment dialog view.
        setContentView(R.layout.comment_add);
        isEditing = false;
        isAdding = true;

        //Load the saved comments
        setSectionScope();
        currentSavedComments = getSavedCommentsList();

        //Initialize Views
        initEditorAddButtons();
        initScopeButtons();

        //Section Scope is the default shown saved comments.
        sectionScope.callOnClick();

    }

    private void openCommentEditorEdit()
    {

        // Set the view and status of the dialog
        setContentView(R.layout.comment_dialog);
        isEditing = true;
        isAdding = false;

        // Load the saved comments (they're editable)
        setSectionScope();
        currentSavedComments = getSavedCommentsList();

        // Initialize views
        initMainNavButtons();
        initMediaCommentsRecycler();

        //Initialize Saved Comments functionality
        initSavedCommentsRecycler();
        initScopeButtons();

        setVisibilities(View.VISIBLE);

    }

    /*
    Configuration Methods
     */
    private void setVisibilities(int status) // Some views are hidden unless certain attributes are activated. I.e. the edit navigates to the home screen with visible extra views for more options.
    {

        editHint = findViewById(R.id.dialog_comment_edit_hint);
        editHint.setVisibility(status);

    }

    private View.OnClickListener getActionButtonClickListener(final ImageButton target) // This method is used to highlight the "scope" buttons when they are clicked
    {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(target.isSelected()) {
                    target.setSelected(false);
                    openCommentEditorHome();
                }else
                {

                    edit.setSelected(false);
                    add.setSelected(false);
                    target.setSelected(true);

                }
            }
        };

    }

    private void updateSavedComments() // Update the DISPLAYED list of saved comments.
    {

        if(savedCommentsList != null)
            if(savedCommentsList.getAdapter() != null)
                savedCommentsList.getAdapter().notifyDataSetChanged();

    }

    /*
    Initialization Methods
     */
    private void initMediaCommentsRecycler()
    {

        reportCommentsList = findViewById(R.id.dialog_comment_list);
        CommentListRecyclerAdapter adapter = new CommentListRecyclerAdapter(getContext(), media.getComments(), this, false, reportCommentsList, (TextView) findViewById(R.id.dialog_comment_list_emptyView));
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        ItemTouchHelper.Callback callback = new CommentListItemTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        adapter.setTouchHelper(helper);
        helper.attachToRecyclerView(reportCommentsList);

        reportCommentsList.setAdapter(adapter);
        reportCommentsList.setLayoutManager(manager);

    }

    private void initMainNavButtons()
    {

        edit = findViewById(R.id.comment_dialog_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommentEditorEdit();
            }
        });
        edit.setSelected(isEditing);


        add = findViewById(R.id.comment_dialog_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommentEditorAdd();
            }
        });

    }

    private void initEditorAddButtons()
    {

        newComment = findViewById(R.id.dialog_add_comment_text);

        //The commit button adds the comment to the report, or finalizes the edit of a comment.
        commit = findViewById(R.id.dialog_comment_commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newComment.getText().toString().isEmpty()) // If the new comment is empty, notify the user of it and do not save
                {

                    new ErrorAlert(getContext(), getContext().getResources().getString(R.string.Error_Empty_Comment));

                }
                else // Save the comment to the list
                {

                    if(isEditing) // If we were editing a comment, the comment should be saved at the old comment's place.
                    {

                        if(isEditingSavedComment) //If the edited comment was from storage, we need to replace it in the temporary list and push it to storage
                        {

                            //Replace the old saved-comment in the temporary list with the new comment
                            targetSavedComments.set(targetSavedComments.indexOf(editedComment), newComment.getText().toString());

                            //Pull a good ol' switch a roo so saveComments() works on the correct file
                            File temp = savedCommentsFile;
                            savedCommentsFile = targetCommentsFile;
                            currentSavedComments = targetSavedComments;
                            saveComments();
                            savedCommentsFile = temp;
                            currentSavedComments = getSavedCommentsList();
                            targetCommentsFile = null;

                            Toast.makeText(getContext(), "Edited Saved Comment", Toast.LENGTH_SHORT).show();
                            savedCommentsList.getAdapter().notifyDataSetChanged();

                        }
                        else
                        {

                            media.getComments().set(media.getComments().indexOf(editedComment), newComment.getText().toString());
                            Toast.makeText(getContext(), "Edited Comment", Toast.LENGTH_SHORT).show();

                        }


                    }
                    else
                    {

                        media.addComments(newComment.getText().toString());
                        Toast.makeText(getContext(), "Added Comment", Toast.LENGTH_SHORT).show();

                    }


                    openCommentEditorHome();

                }
            }
        });

        initSavedCommentsRecycler();

        //The save button 'saves' the comment onto the device to be used in future comments
        save = findViewById(R.id.dialog_comment_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(newComment.getText().toString().replaceAll("/s|\n", "").isEmpty())
                {
                    new ErrorAlert(getContext(), "You must first type a comment to save it!");
                    newComment.setText("");
                    return;
                }
                currentSavedComments.add(newComment.getText().toString());
                saveComments();
                updateSavedComments();

            }
        });

        // The back button exits editing or adding a comment
        Button back = findViewById(R.id.dialog_comment_add_cancel);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditing)
                    openCommentEditorEdit();
                else
                    openCommentEditorAdd();
            }
        });

    }

    private void initScopeButtons()
    {

        globalScope = findViewById(R.id.dialog_comment_global_scope);
        globalScope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalScope.setTextColor(ContextCompat.getColor(getContext(), R.color.WR_orange_dark));
                systemScope.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_unfocused));
                sectionScope.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_unfocused));
                setGlobalScope();
                isSectionScope = false;
                currentSavedComments.clear();
                currentSavedComments.addAll(getSavedCommentsList());
                updateSavedComments();
            }
        });
        systemScope = findViewById(R.id.dialog_comment_system_scope);
        systemScope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systemScope.setTextColor(ContextCompat.getColor(getContext(), R.color.WR_orange_dark));
                sectionScope.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_unfocused));
                globalScope.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_unfocused));
                setSystemScope();
                isSectionScope = false;
                currentSavedComments.clear();
                currentSavedComments.addAll(getSavedCommentsList());
                updateSavedComments();
            }
        });
        sectionScope = findViewById(R.id.dialog_comment_section_scope);
        sectionScope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sectionScope.setTextColor(ContextCompat.getColor(getContext(), R.color.WR_orange_dark));
                systemScope.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_unfocused));
                globalScope.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_unfocused));
                setSectionScope();
                isSectionScope = true;
                currentSavedComments.clear();
                currentSavedComments.addAll(getSavedCommentsList());
                updateSavedComments();
            }
        });

    }

    private void initSavedCommentsRecycler()
    {

        savedCommentsList = findViewById(R.id.dialog_saved_comment_list);
        CommentListRecyclerAdapter adapter = new CommentListRecyclerAdapter(getContext(), currentSavedComments, this, true, savedCommentsList,findViewById(R.id.dialog_comment_saved_comments_emptyView));
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        ItemTouchHelper.Callback callback = new CommentListItemTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        adapter.setTouchHelper(helper);
        helper.attachToRecyclerView(savedCommentsList);

        savedCommentsList.setAdapter(adapter);
        savedCommentsList.setLayoutManager(manager);

    }

    /*
    Save and Get methods
     */

    private void saveComments() // Write saved comments to file.
    {

        if(savedCommentsFile != null)
        {

            try {

                //If the file does not exist create it.
                savedCommentsFile.createNewFile();

                //Write to the text file.
                FileWriter writer = new FileWriter(savedCommentsFile);
                for(String aComment  : currentSavedComments)
                {

                    writer.write(aComment.replaceAll("\n", "//n") + "["); // Replace line breaks with //n and end the comment with a '[' to make loading easier.

                }
                writer.close();

            }
            catch(IOException e)
            {

                e.printStackTrace();
                Toast.makeText(getContext(), "File Read Error", Toast.LENGTH_SHORT).show();

            }

        }

    }

    public void appendComment(String comment)
    {

        if(isAdding) // If we are creating a new comment
        {

            // Add the saved comment (comment) onto the new comment EditText field.
            String newText = newComment.getText() + comment;
            newComment.setText(newText);
            Toast.makeText(getContext(), "Added Saved Comment", Toast.LENGTH_SHORT).show();

        }
        else if(!isEditing) // If we are not adding a new comment, but also not in edit mode
        {

            //Add the saved comment directly to the comments on the report.
            media.getComments().add(comment);
            reportCommentsList.getAdapter().notifyDataSetChanged(); // Update the recycler showing the media comments
            Toast.makeText(getContext(), "Added Saved Comment", Toast.LENGTH_SHORT).show();


        }

    }

    public void removeSavedComment(String comment)
    {

        currentSavedComments.remove(comment);
        saveComments();

    }

    public void editSavedComment(String comment)
    {

        //Set the target file and target saved comments because when the add-comment editor is called, the
        //saved comment file and list is reinitialized.
        targetSavedComments = currentSavedComments;
        targetCommentsFile = savedCommentsFile;

        openCommentEditorAdd();
        isEditing = true;
        isEditingSavedComment = true;
        save.setVisibility(View.INVISIBLE);
        newComment.setText(comment);
        editedComment = comment;

    }

    //Return an arraylist of the stored comments in the currently selected scope
    private ArrayList<String> getSavedCommentsList()
    {

        //Check if the saved comments file is not defined
        if(savedCommentsFile != null)
        {

            try
            {

                //Create the file if it does not exist
                savedCommentsFile.createNewFile();

                //Create an arraylist to store the comments
                ArrayList<String> loadedComments = new ArrayList<>();

                //Read the file
                BufferedReader reader = new BufferedReader(new FileReader(savedCommentsFile));
                String line;

                //Loop through each line and split it at all '[' characters.
                //Add line breaks whenever the comment contains "//n"
                while((line = reader.readLine()) != null) {
                    for (String savedComment : line.split("\\[")) // Split the line along the open brackets. This character denotes the end of a comment.
                    {

                        if (savedComment.contains("//n")) // If this comment contains intentional line breaks, insert them as necessary.
                        {

                            //Add the saved comment in its original form
                            loadedComments.add(savedComment.replaceAll("//n", "\n"));

                        } else
                            loadedComments.add(savedComment);

                    }

                }

                return loadedComments;

            }
            catch(IOException e)
            {

                Toast.makeText(getContext(), "File Error", Toast.LENGTH_SHORT).show();

            }

        }

        return new ArrayList<>();

    }

    //Look for saved comments across the entire app. Globally.
    private void setGlobalScope()
    {

        savedCommentsFile = new File(commentDirectory, "Global.txt");

    }

    //Look for saved comments across the entire parent system.
    private void setSystemScope()
    {

        savedCommentsFile = new File(systemCommentDirectory, section.getSystem().getDisplayName() + ".txt");

    }

    //Look for saved coments only within the section being edited. Sections are specific to each "checkbox","slider","defect",etc.
    private void setSectionScope()
    {

        //Include the simple name in order to avoid accessing the same file with similar named sections. "Context_Media" appears in all systems, but "RoofSections_CONTEXT_MEDIA" appears once.
        savedCommentsFile = new File(sectionCommentDirectory, section.getSystem().getDisplayName() + "_" + section.getName() + ".txt");

    }

    //This method opens after the comment the user wants to edit has been selected.
    public void editComment(String comment)
    {

        openCommentEditorAdd();
        isEditing = true;
        isEditingSavedComment = false;
        newComment.setText(comment);
        editedComment = comment;

    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

}
