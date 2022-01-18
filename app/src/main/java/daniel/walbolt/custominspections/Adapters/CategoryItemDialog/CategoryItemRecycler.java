package daniel.walbolt.custominspections.Adapters.CategoryItemDialog;

import android.nfc.tech.NdefFormatable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.DescriptionDialog;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryGroup;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Checkbox;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.DefectItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.Numeric;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.ObservationItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.RestrictionItem;
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

    public CategoryItemRecycler(ArrayList<CategoryItem> items)
    {

        this.items = items;

    }

    public CategoryItemRecycler(ArrayList<CategoryItem> items, CategoryDialog categoryDialog)
    {

        this.items = items;
        this.isInDialog = true;
        this.categoryDialog = categoryDialog;

    }

    //Override this method because we need to load many types of layouts (this can be extended to more)!
    //One layout is a text view and a nested recycler view (CategoryGroup).
    // Otherwise, the default layout is just a CheckBox item.
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
        else if(item instanceof Numeric)
            return 4;
        else if(item instanceof ObservationItem)
            return 5;
        else if(item instanceof RestrictionItem)
            return 6;
        else if(item instanceof DefectItem)
            return 7;
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
        else if(viewType == 2)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_checkbox_recycler, parent, false);
        else if(viewType == 3)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_slider_recycler, parent, false);
        else if(viewType == 4)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_numeric_recycler, parent, false);
        else if(viewType == 5)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.observation_item, parent, false);
        else if(viewType == 6)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restriction_item, parent, false);
        else if(viewType == 7)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.defect_item, parent, false);

        return new ItemHolder(v, viewType); // Initialize the view

    }

    //Combine the ViewHolder class and the View itself.
    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position)
    {

        //Get the item associated with the view
        CategoryItem item = items.get(position);

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
                initBasicInfoItem(holder, item);

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

        }



    }

    private void initCheckBoxItem(ItemHolder holder, Checkbox item)
    {

        initBasicInfoItem(holder, item);

        //Implement checkbox functionality
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isInDialog) // If the recycler is in a dialog, remove checkbox functionality
                {

                    item.setApplicability(isChecked);
                    holder.comments.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                }
                else
                    holder.comments.setVisibility(View.GONE);

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
            item.initDialogRecycler(holder.itemRecycler, categoryDialog);
        else
            item.initRecycler(holder.itemRecycler); // Initialize the recycler to display the group's contents

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
                if(!isInDialog) // If the recycler is in a dialog, remove checkbox functionality
                {

                    item.setApplicability(isChecked);
                    holder.slider.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    holder.comments.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                }

            }
        });
        holder.checkBox.setChecked(item.isApplicable()); // Set the current state of the item. Doing this after the listener is set means the function is performed

        //Customize the slider view
        holder.slider.getConfigBuilder().sectionCount(item.getMax()-1).animDuration(500).build();
        holder.slider.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();

                // Populate the array with the desired content
                int index = 0;
                for(String content : item.getContent())
                {

                    array.put(index, content);
                    index++;

                }

                return array;
            }
        }); // Custom slider bar requires an interesting way of populating it.

    }

    private void initNumericItem(ItemHolder holder, Numeric item)
    {

        //Initialize common views of an info-item.
        initBasicInfoItem(holder, item);

        //Create checkbox functionality
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isInDialog)
                    buttonView.setChecked(false);
                else
                {

                    item.setApplicability(isChecked);
                    holder.content.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    holder.comments.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                }
            }
        });
        holder.checkBox.setChecked(item.isApplicable());

        holder.numeric.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                item.setText(holder.numeric.getText().toString());
            }
        });

        holder.numeric.setText(item.getText()); // Set the current text that is saved

    }

    private void initObservationItem(ItemHolder holder, ObservationItem observation)
    {

        initBasicInfoItem(holder, observation);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isInDialog)
                    buttonView.setChecked(false);
                else
                {

                    observation.setApplicability(isChecked);
                    holder.content.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                    holder.comments.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    holder.pictures.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    enablePictures(holder, observation);

                }
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
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isInDialog)
                    buttonView.setChecked(false);
                else
                {

                    restriction.setApplicability(isChecked);
                    holder.content.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                    holder.comments.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    holder.pictures.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    enablePictures(holder, restriction);

                }
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
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isInDialog)
                    buttonView.setChecked(false);
                else
                {

                    defect.setApplicability(isChecked);
                    holder.content.setVisibility(isChecked ? View.VISIBLE : View.GONE);

                    holder.comments.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    holder.pictures.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    enablePictures(holder, defect);

                }
            }
        });
        holder.checkBox.setChecked(defect.isApplicable());

        //Enable defect image recycler
        defect.initMedia(holder.mediaRecycler, holder.recyclerEmptyView); // Initialize the recycler for the media


        holder.maintain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                defect.setMonitorAndMaintain(isChecked);
                defect.setSeverity(DefectItem.SEVERITY.MONITOR_MAINTAIN);
                holder.severity.setProgress(defect.getSeverity().getProgress());
            }
        });
        holder.maintain.setChecked(defect.isMonitorAndMaintain());

        holder.repair.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                defect.setMonitorAndMaintain(!isChecked);
                holder.severityContent.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        holder.repair.setChecked(!defect.isMonitorAndMaintain());

        holder.severity.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();

                // Populate the array with the desired content
                array.put(0, "Low");
                array.put(1, "Medium");
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
                        defect.setSeverity(DefectItem.SEVERITY.LOW);
                        break;
                    case 1:
                        defect.setSeverity(DefectItem.SEVERITY.MEDIUM);
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

        EditText numeric;
        LinearLayout content;

        //In the case the CategoryItem is a CategoryGroup
        RecyclerView itemRecycler;

        //In the case the CategoryItem has pictures
        RecyclerView mediaRecycler;
        ImageButton pictures;
        TextView recyclerEmptyView;
        
        //In the case the CategoryItem is a defect
        RadioButton maintain;
        RadioButton repair;
        BubbleSeekBar severity;
        LinearLayout severityContent;


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
                itemRecycler = itemView.findViewById(R.id.category_group_container);

            }
            else if(viewType == 2) // The item is a CheckBox
            {

                //Initialize the views for CheckBoxes
                checkBox = itemView.findViewById(R.id.info_item_checkbox);
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

                numeric = itemView.findViewById(R.id.info_item_numeric_input);
                content = itemView.findViewById(R.id.info_item_numeric_content);
                initCheckableItemCommonViews();

            }
            else if(viewType == 5) // Item is an ObservationItem
            {

                content = itemView.findViewById(R.id.observation_item_content);
                pictures = itemView.findViewById(R.id.info_item_picture);
                mediaRecycler = itemView.findViewById(R.id.observation_item_media_recycler);
                recyclerEmptyView = itemView.findViewById(R.id.observation_item_media_emptyView);
                initCheckableItemCommonViews();

            }
            else if(viewType == 6) // Item is a restriction
            {

                content = itemView.findViewById(R.id.restriction_item_content);
                pictures = itemView.findViewById(R.id.info_item_picture);
                mediaRecycler = itemView.findViewById(R.id.restriction_item_media_recycler);
                recyclerEmptyView = itemView.findViewById(R.id.restriction_item_media_emptyView);
                initCheckableItemCommonViews();

            }
            else if(viewType == 7) // Item is a defect
            {

                content = itemView.findViewById(R.id.defect_item_content);
                pictures = itemView.findViewById(R.id.info_item_picture);
                mediaRecycler = itemView.findViewById(R.id.defect_item_media_recycler);
                recyclerEmptyView = itemView.findViewById(R.id.defect_item_media_emptyView);
                
                maintain = itemView.findViewById(R.id.defect_item_monitor);
                repair = itemView.findViewById(R.id.defect_item_repair);
                
                severity = itemView.findViewById(R.id.defect_item_severity);
                severityContent = itemView.findViewById(R.id.defect_item_severity_content);

                initCheckableItemCommonViews();

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
