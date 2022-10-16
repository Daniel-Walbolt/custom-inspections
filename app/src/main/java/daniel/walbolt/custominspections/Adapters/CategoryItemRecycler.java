package daniel.walbolt.custominspections.Adapters;

import android.animation.LayoutTransition;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.HashMap;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.DescriptionDialog;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.AdvancedCheckbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Checkbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.DefectItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.ObservationItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.PictureItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.RestrictionItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.SettingItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Slider;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.R;

public class CategoryItemRecycler extends RecyclerView.Adapter<CategoryItemRecycler.ItemHolder>
{

    // The CategoryItem recycler creates a list of Information CategoryItems that can each have their own behavior using pop-up dialogs.
    // This recycler is used in both the category dialog and the system-page itself.

    private ArrayList<CategoryItem> items;

    private boolean isInDialog; // If this is true, the items are being displayed in an editor dialog, and should not have normal functionality
    private CategoryDialog categoryDialog; // Dialog reference for creation of new dialogs (otherwise they're small if we use context of a recycler item)

    private RecyclerView recyclerView;

    public CategoryItemRecycler(ArrayList<CategoryItem> items, TextView emptyView, RecyclerView categoryRecycler)
    {

        this.items = items;
        this.recyclerView = categoryRecycler;

        if(emptyView != null)
        {

            //If the CategoryItems provided are empty, then display the empty view.
            // The empty view informs the user there is nothing to display here.
            if(items.isEmpty())
            {

                categoryRecycler.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);

            }
            else
            {

                categoryRecycler.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);

            }


            //Create a data observer to update the visibility of the empty view if the data becomes empty, or not empty.
            registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged()
                {

                    if(getItemCount() == 0)
                    {

                        categoryRecycler.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);

                    }
                    else
                    {

                        categoryRecycler.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);

                    }

                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount)
                {

                    if(getItemCount() == 0)
                    {

                        categoryRecycler.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);

                    }
                    else
                    {

                        categoryRecycler.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);

                    }

                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount)
                {

                    if(getItemCount() == 0)
                    {

                        categoryRecycler.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);

                    }
                    else
                    {

                        categoryRecycler.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);

                    }
                }
            });

        }

    }

    public CategoryItemRecycler(ArrayList<CategoryItem> items, CategoryDialog categoryDialog, TextView emptyView, RecyclerView categoryRecycler)
    {

        this(items, emptyView, categoryRecycler);
        this.isInDialog = true;
        this.categoryDialog = categoryDialog;

    }

    //Override this method because we need to load many types of layouts (this can be extended to more)!
    @Override
    public int getItemViewType(int position)
    {

        CategoryItem item = items.get(position);

        if(item instanceof CategoryGroup)
            return 1;
        else if(item instanceof Checkbox)
            return 2;
        else if(item instanceof Slider)
            return 3;
        else if(item instanceof Numeric) { if (((Numeric)item).isVersion2()) { return 5;} else return 4;}
        else if(item instanceof ObservationItem)
            return 6;
        else if(item instanceof RestrictionItem)
            return 7;
        else if(item instanceof SettingItem)
            return 8;
        else if(item instanceof DefectItem)
            return 9;
        else if(item instanceof PictureItem)
            return 10;
        else if(item instanceof AdvancedCheckbox)
            return 11;
        else
            return 0;

    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View v = null;
        //There are several types of layouts available.
        // This recycler handles them all because they all need to exist in the same container.
        // Category groups, Check boxes, numeric inputs, and sliders have their own views and internal IDs.

        if (viewType == 1)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_group, parent, false);
        else if(viewType == 2 || viewType == 8)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_checkbox_recycler, parent, false);
        else if(viewType == 3)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_slider_recycler, parent, false);
        else if(viewType == 4)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_numeric_recycler, parent, false);
        else if(viewType == 5)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_numeric_2_recycler, parent, false);
        else if(viewType == 6)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.observation_item, parent, false);
        else if(viewType == 7)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restriction_item, parent, false);
        else if(viewType == 9)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.defect_item, parent, false);
        else if(viewType == 10)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        else if(viewType == 11)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_advanced_checkbox, parent, false);

        return new ItemHolder(v, viewType); // Initialize the view

    }

    //Combine the ViewHolder class and the View itself.
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position)
    {

        //Get the item associated with the view
        CategoryItem item = items.get(holder.getAdapterPosition());

        //Items in the dialog are not completely initialized.
        //The ItemHolder is turned into a click listener.
        if(isInDialog)
        {

            //IF the recycler view is inside the category dialog, clicking on any item will start editing it
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryDialog.editCategoryItem(item);
                }
            });

            if(item instanceof CategoryGroup)
                initCategoryGroup(holder, (CategoryGroup) item);
            else
            {

                holder.checkBox.setChecked(item.isApplicable());
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        compoundButton.setChecked(item.isApplicable()); // Button should not work in the dialog.
                    }
                });
                initBasicInfoItem(holder, item);

            }

        }
        else
        {

            //Initialize the views based off the type of the item
            if(item instanceof CategoryGroup)
                initCategoryGroup(holder, (CategoryGroup) item);
            else if(item instanceof Checkbox)
                initCheckBoxItem(holder, (Checkbox) item);
            else if(item instanceof Slider)
                initSliderItem(holder, (Slider) item);
            else if(item instanceof Numeric)
                initNumericItem(holder, (Numeric) item);
            else if(item instanceof ObservationItem)
                initObservationItem(holder, (ObservationItem) item);
            else if(item instanceof RestrictionItem)
                initRestrictionItem(holder, (RestrictionItem) item);
            else if(item instanceof DefectItem)
                initDefectItem(holder, (DefectItem) item);
            else if(item instanceof SettingItem)
                initSettingItem(holder, (SettingItem) item);
            else if(item instanceof PictureItem)
                initPictureItem(holder, (PictureItem) item);
            else if(item instanceof AdvancedCheckbox)
                initAdvancedCheckbox(holder, (AdvancedCheckbox) item);

        }



    }

    //Add an animation to a view when its visibility changes
    private void animateViewOnVisibilityChange(View animatedView, boolean visible)
    {

        Animation animation;

        if(visible)
            animation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.expand_down);
        else
            animation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.expand_up);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if(visible)
                    animatedView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!visible)
                    animatedView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        animatedView.startAnimation(animation);

    }

    //Add an animation to buttons when its visibility changes
    private void animateButtonVisibilityChange(View animatedView, boolean visible)
    {

        Animation animation;
        if(visible)
            animation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.scale_up);
        else
            animation = AnimationUtils.loadAnimation(animatedView.getContext(), R.anim.scale_down);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if(visible)
                    animatedView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!visible)
                    animatedView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animatedView.startAnimation(animation);

    }

    private void initCheckBoxItem(ItemHolder holder, Checkbox item)
    {

        initBasicInfoItem(holder, item);

        //Implement checkbox functionality
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setApplicability(isChecked);
            }
        });

        holder.checkBox.setChecked(item.isApplicable()); // Set the current state of the item. Doing this after the listener is set means the function is performed


    }

    private void initCategoryGroup(ItemHolder holder, CategoryGroup item)
    {

        // Find and set the name of this group
        holder.name.setText(item.getName());

        //Initialize the groups recyclerview, and populate it
        if(isInDialog)
            item.initDialogRecycler(holder.itemRecycler, categoryDialog, holder.recyclerEmptyView);
        else
            item.initRecycler(holder.itemRecycler, holder.recyclerEmptyView); // Initialize the recycler to display the group's contents

    }

    //This method finds and assigns values to all common views between InfoItems
    private void initBasicInfoItem(ItemHolder holder, CategoryItem item)
    {

        //Find and set the name of the item
        holder.name = holder.itemView.findViewById(R.id.info_item_title);
        holder.name.setText(item.getName());

        holder.comments = holder.itemView.findViewById(R.id.info_item_comment);
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.openCommentDialog(holder.itemView.getContext());
            }
        });

        holder.description = holder.itemView.findViewById(R.id.info_item_description);
        if(item.getDescription() != null && !isInDialog) // If the item has a defined description open it when the icon is clicked (NOT IN DIALOG)
        {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DescriptionDialog(holder.itemView.getContext(), item.getDescription());
                }
            });
        }

    }

    private void initSliderItem(ItemHolder holder, Slider item)
    {

        initBasicInfoItem(holder, item);

        //Implement checkbox functionality
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setApplicability(isChecked);
                    animateViewOnVisibilityChange(holder.slider, isChecked);
            }
        });

        holder.checkBox.setChecked(item.isApplicable()); // Set the current state of the item. Doing this after the listener is set means the function is performed

        //Customize the slider view
        holder.slider.getConfigBuilder().sectionCount(item.getMax()-1)
                .max(item.getMax()).min(item.getMin()).build();
        holder.slider.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();

                // Populate the array with the desired content
                int index = 0;

                //Add the default options to the slider, minimum length of the slider is 2.
                array.put(0, "Default 1");
                array.put(1, "Default 2");

                for(String content : item.getContent())
                {

                    array.put(index, content);
                    index++;

                }

                return array;
            }
        }); // Custom slider bar requires an interesting way of populating it.

        holder.slider.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                item.setProgress(progress);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });

        holder.slider.setProgress(item.getProgress());

    }

    private void initNumericItem(ItemHolder holder, Numeric item)
    {

        //Initialize common views of an info-item.
        if (!item.isVersion2())
            initBasicInfoItem(holder, item);
        else
            holder.name.setText(item.getName());

        //Create checkbox functionality
        if (!item.isVersion2())
        {

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    item.setApplicability(isChecked);
                    animateViewOnVisibilityChange(holder.content, isChecked);
                }
            });

            holder.checkBox.setChecked(item.isApplicable());

        }

        holder.numericInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setText(holder.numericInput.getText().toString());
            }
        });

        holder.numericInput.setText(item.getText()); // Set the current text that is saved
        holder.numericUnit.setText(item.getUnit()); // Set the unit of the numeric entry

    }

    private void initObservationItem(ItemHolder holder, ObservationItem observation)
    {

        initBasicInfoItem(holder, observation);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                observation.setApplicability(isChecked);
                animateViewOnVisibilityChange(holder.content, isChecked);

                animateButtonVisibilityChange(holder.comments, isChecked);
                animateButtonVisibilityChange(holder.pictures, isChecked);
                enablePictures(holder, observation);

            }
        });
        holder.checkBox.setChecked(observation.isApplicable());

        observation.initMedia(holder.mediaRecycler, holder.recyclerEmptyView); // Initialize the recycler for the media

    }

    private void initRestrictionItem(ItemHolder holder, RestrictionItem restriction)
    {

        initBasicInfoItem(holder, restriction);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                restriction.setApplicability(isChecked);
                animateViewOnVisibilityChange(holder.content, isChecked);

                animateButtonVisibilityChange(holder.comments, isChecked);
                animateButtonVisibilityChange(holder.pictures, isChecked);
                enablePictures(holder, restriction);

            }
        });
        holder.checkBox.setChecked(restriction.isApplicable());

        restriction.initMedia(holder.mediaRecycler, holder.recyclerEmptyView);

    }

    private void initDefectItem(ItemHolder holder, DefectItem defect)
    {
        
        initBasicInfoItem(holder, defect);

        //Enable defect checkbox functionality
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                defect.setApplicability(isChecked);
                animateViewOnVisibilityChange(holder.content, isChecked);
                animateButtonVisibilityChange(holder.comments, isChecked);
                animateButtonVisibilityChange(holder.pictures, isChecked);
                enablePictures(holder, defect);
            }
        });
        holder.checkBox.setChecked(defect.isApplicable());

        //Enable defect image recycler
        defect.initMedia(holder.mediaRecycler, holder.recyclerEmptyView); // Initialize the recycler for the media

        holder.severity.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();

                // Populate the array with the desired content
                array.put(0, "Maintain");
                array.put(1, "Mild");
                array.put(2, "High");

                return array;
            }
        }); // Custom slider bar requires an interesting way of populating it.

        holder.severity.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                switch(progress)
                {

                    case 0:
                        defect.setSeverity(DefectItem.SEVERITY.MAINTAIN);
                        break;
                    case 1:
                        defect.setSeverity(DefectItem.SEVERITY.MILD);
                        break;
                    case 2:
                        defect.setSeverity(DefectItem.SEVERITY.HIGH);
                        break;

                }
            }
        });

        //Set the current Severity of the defect
        holder.severity.setProgress(defect.getSeverity().getProgress());

    }

    private void initSettingItem(ItemHolder holder, SettingItem settingItem)
    {

        initBasicInfoItem(holder, settingItem);
        holder.comments.setVisibility(View.GONE);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingItem.setApplicability(isChecked);
                settingItem.getCheckEvent().onCheckedChanged(buttonView, isChecked);
            }
        });
        holder.checkBox.setChecked(settingItem.isApplicable());

    }

    private void initPictureItem(ItemHolder holder, PictureItem pictureItem)
    {

        holder.name.setText(pictureItem.getName());
        System.out.println("Status: " + pictureItem.getCompletionStatus());
        holder.pictureView.setImageURI(pictureItem.getCompletionStatus() ? pictureItem.getMedia().getURI(holder.pictureView.getContext()) : null);
        holder.picItemContent.setOnClickListener((e) -> {
           pictureItem.getMedia().takePicture(holder.picItemContent.getContext());
        });

    }

    private void initAdvancedCheckbox(ItemHolder holder, AdvancedCheckbox advCheckbox)
    {

        //Set the title of the section
        holder.name.setText(advCheckbox.getName());

        //Create a string to tell the user what the selection type is for this checkbox
        StringBuilder type = new StringBuilder("");
        switch (advCheckbox.getSelectionType())
        {
            case "ALL":
                type.append("Select all that apply");
                break;
            case "MULTI":
                type.append("Select up to " + advCheckbox.getSelectionAmount());
                break;
            case "SINGLE":
                type.append("Select one");
        }

        //Show the selection type text.
        holder.selectionType.setText(type);

        //Get the checkboxes (represented by string and booleans) that we're rendering.
        HashMap<String, Boolean> checkBoxes = advCheckbox.getCheckboxes();

        //Separate the names from from the hashmap.
        ArrayList<String> names = new ArrayList<>(checkBoxes.keySet());
        ArrayList<CheckBox> checkboxes = new ArrayList<>(); // Temporarily store the checkboxes we create. This will be used for listeners later.
        ArrayList<TextView> checkboxTitles = new ArrayList<>(); // Temporarily store the titles of each checkbox

        LinearLayout row = new LinearLayout(holder.itemView.getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        
        // Create the checkbox options by inflating an xml file
        for (String name : names)
        {

            //Get the container for the checkbox and the title
            LinearLayout checkbox = (LinearLayout) LayoutInflater.from(holder.content.getContext()).inflate(R.layout.checkbox_option, row, false);

            //Get the 4 different checkboxes from the view
            CheckBox cb = checkbox.findViewById(R.id.checkbox_box);
            TextView title = checkbox.findViewById(R.id.checkbox_title);

            checkbox.removeAllViews(); // Remove both the checkbox and the textview from the layout, this is necessary for adding them somewhere else

            title.setText(name);
            checkboxes.add(cb);
            checkboxTitles.add(title);

        }
        
        //Loop through all the checkboxes we just created
        for (int count = 0; count < checkboxes.size(); count++)
        {

            CheckBox button = checkboxes.get(count);
            TextView title = checkboxTitles.get(count);

            if (count % 4 == 0 || count == checkboxes.size() - 1) // If we've registered 4 checkboxes, go to next row
            {
                holder.content.addView(row);
                row = new LinearLayout(holder.itemView.getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
            }

            button.setOnCheckedChangeListener(advCheckbox.getListener(title.getText().toString(), checkboxes));
            button.setChecked(checkBoxes.getOrDefault(title.getText().toString(), false));
            row.addView(button);
            row.addView(title);

        }

    }

    private void enablePictures(ItemHolder holder, CategoryItem item)
    {

        //Every section that can store pictures will use this listener on their picture view.
        holder.pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //Create a media object to store the reference to the image and handle saving and loading it
                InspectionMedia imageController = new InspectionMedia(item);

                //Add the media object to the item
                item.addMedia(imageController);

                imageController.takePicture(holder.itemView.getContext());

                //Once the user accesses the camera, they can NOT go back until after confirming a picture.

            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder
    {

        //Every checkbox item has these views
        TextView name;
        CheckBox checkBox;
        ImageButton comments;
        ImageButton description;

        BubbleSeekBar slider;

        EditText numericInput;
        TextView numericUnit;
        LinearLayout content;

        //In the case the CategoryItem is a CategoryGroup
        RecyclerView itemRecycler;

        //In the case the CategoryItem has pictures
        RecyclerView mediaRecycler;
        ImageButton pictures;
        TextView recyclerEmptyView;
        
        //In the case the CategoryItem is a defect
        BubbleSeekBar severity;

        //In the case the CategoryItem is a PictureItem
        ImageView pictureView;

        //In the case the CategoryItem is an advanced checkbox
        TextView selectionType;

        //In the case the CategoryItem is a PictureItem, relative layout is used for its content
        RelativeLayout picItemContent;

        /*

        The ItemHolder's job is to properly assign the right Views/ID look-ups to the proper variables
        based off the type of view that it is being assigned to. Different views have different IDs, except for views in-common.

         */
        public ItemHolder(@NonNull View itemView, int viewType)
        {

            super(itemView);

            if(viewType == 1) //  The category item is a CategoryGroup
            {

                //Find CategoryGroup views
                name = itemView.findViewById(R.id.category_group_title);
                recyclerEmptyView = itemView.findViewById(R.id.category_group_emptyView);
                itemRecycler = itemView.findViewById(R.id.category_group_container);

            }
            else if(viewType == 2 || viewType == 8) // The item is a CheckBox
            {

                //Initialize the views for CheckBoxes
                initCheckableItemCommonViews();


            }
            else if(viewType == 3) // Item is a Slider
            {

                //Initialize the view for the slider
                slider = itemView.findViewById(R.id.info_item_slider_bar);
                initCheckableItemCommonViews();


            }
            else if(viewType == 4) // Item is a Numeric Entry
            {

                numericInput = itemView.findViewById(R.id.info_item_numeric_input);
                numericUnit = itemView.findViewById(R.id.info_item_numeric_unit);
                content = itemView.findViewById(R.id.info_item_numeric_content);
                initCheckableItemCommonViews();

            }
            else if(viewType == 5) // Item is a Numeric Entry, version 2.
            {

                name = itemView.findViewById(R.id.info_item_title);
                numericInput = itemView.findViewById(R.id.info_item_numeric_input);
                numericUnit = itemView.findViewById(R.id.info_item_numeric_unit);

            }
            else if(viewType == 6) // Item is an ObservationItem
            {

                content = itemView.findViewById(R.id.observation_item_content);

                //Add layout change animation
                content.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

                pictures = itemView.findViewById(R.id.info_item_picture);
                mediaRecycler = itemView.findViewById(R.id.observation_item_media_recycler);
                recyclerEmptyView = itemView.findViewById(R.id.observation_item_media_emptyView);
                initCheckableItemCommonViews();

            }
            else if(viewType == 7) // Item is a restriction
            {

                content = itemView.findViewById(R.id.restriction_item_content);
                pictures = itemView.findViewById(R.id.info_item_picture);
                mediaRecycler = itemView.findViewById(R.id.restriction_item_media_recycler);
                recyclerEmptyView = itemView.findViewById(R.id.restriction_item_media_emptyView);
                initCheckableItemCommonViews();

            }
            else if(viewType == 9) // Item is a defect
            {

                content = itemView.findViewById(R.id.defect_item_content);

                pictures = itemView.findViewById(R.id.info_item_picture);
                mediaRecycler = itemView.findViewById(R.id.defect_item_media_recycler);
                recyclerEmptyView = itemView.findViewById(R.id.defect_item_media_emptyView);
                
                severity = itemView.findViewById(R.id.defect_item_severity); // The severity slider

                initCheckableItemCommonViews();

            }
            else if(viewType == 10) // Item is a PictureItem
            {

                picItemContent = itemView.findViewById(R.id.picture_item_container);

                name = itemView.findViewById(R.id.picture_item_title);
                pictureView = itemView.findViewById(R.id.picture_item_picture);

            }
            else if(viewType == 11) // Item is an Advanced Checkbox
            {

                content = itemView.findViewById(R.id.advanced_checkbox_content);
                name = itemView.findViewById(R.id.advanced_checkbox_title);
                selectionType = itemView.findViewById(R.id.advanced_checkbox_selection_type);

            }


        }

        private void initCheckableItemCommonViews()
        {

            name = itemView.findViewById(R.id.info_item_title);
            checkBox = itemView.findViewById(R.id.info_item_checkbox);
            comments = itemView.findViewById(R.id.info_item_comment);
            description = itemView.findViewById(R.id.info_item_description);

        }

    }

}
