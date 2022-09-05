package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.ObservationItem;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class Observations extends Category{
    public Observations(System parent) {
        super(TYPE.OBSERVATION, parent);
    }

    @Override
    public void loadToPage(LinearLayout pageLayout)
    {

        //Get the layout of an information category
        LinearLayout categoryLayout = (LinearLayout) LayoutInflater.from(pageLayout.getContext()).inflate(R.layout.observations_category, pageLayout, false);

        //Add layout change animation
        categoryLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        //Initialize the necessary views inside the category, including the Category Items
        categoryRecycler = categoryLayout.findViewById(R.id.observations_category_items);
        emptyView = categoryLayout.findViewById(R.id.observations_category_emptyView);
        ((SimpleItemAnimator) categoryRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        initRecycler();

        //Edit button which opens the editor of the information category
        ImageButton edit = categoryLayout.findViewById(R.id.observations_category_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CategoryDialog(pageLayout.getContext(), thisClass()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        updateRecycler();
                    }
                });
            }
        });

        //Add the entire category to the page
        pageLayout.addView(categoryLayout);

    }
}
