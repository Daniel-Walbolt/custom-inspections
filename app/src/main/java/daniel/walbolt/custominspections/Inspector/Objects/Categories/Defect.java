package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class Defect extends Category{
    public Defect(System parent) {
        super(TYPE.DEFECT, parent);
    }

    @Override
    public void loadToPage(LinearLayout pageLayout) {

        //Get the layout of an information category
        LinearLayout categoryLayout = (LinearLayout) LayoutInflater.from(pageLayout.getContext()).inflate(R.layout.defects_category, pageLayout, false);

        //Add layout change animation
        categoryLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);


        //Initialize the necessary views inside the category, including the Category Items
        categoryRecycler = categoryLayout.findViewById(R.id.defects_category_items);
        emptyView = categoryLayout.findViewById(R.id.defects_category_emptyView);
        initRecycler();

        //Edit button which opens the editor of the information category
        ImageButton edit = categoryLayout.findViewById(R.id.defects_category_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CategoryDialog(pageLayout.getContext(), thisClass()).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        //Update the category's recycler
                        updateRecycler();
                    }
                });
            }
        });

        //Add the entire category to the page
        pageLayout.addView(categoryLayout);

    }
}
