package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class Information extends Category
{

    //The Information Category contains simple CategoryItems. For the most part, they only contain true false values.

    public Information(System parent) {
        super(TYPE.INFORMATION, parent);
    }

    @Override
    public void loadToPage(LinearLayout pageLayout)
    {

        //Get the layout of an information category
        LinearLayout categoryLayout = (LinearLayout) LayoutInflater.from(pageLayout.getContext()).inflate(R.layout.information_category, pageLayout, false);

        //Add layout change animation
        categoryLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        //Initialize the necessary views inside the category, including the Category Items
        categoryRecycler = categoryLayout.findViewById(R.id.information_category_items);
        emptyView = categoryLayout.findViewById(R.id.information_category_emptyView);
        initRecycler();

        //Edit button which opens the editor of the information category
        ImageButton edit = categoryLayout.findViewById(R.id.information_category_edit);
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
