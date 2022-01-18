package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import daniel.walbolt.custominspections.Adapters.CategoryItemDialog.CategoryItemRecycler;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CategoryDialog;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class Restrictions extends Category{

    public Restrictions(System parent) {
        super(TYPE.RESTRICTION, parent);
    }

    @Override
    public void loadToPage(LinearLayout pageLayout)
    {

        //Get the layout of an information category
        View categoryLayout = LayoutInflater.from(pageLayout.getContext()).inflate(R.layout.restrictions_category, pageLayout, false);

        //Initialize the necessary views inside the category, including the Category Items
        categoryRecycler = categoryLayout.findViewById(R.id.restriction_category_items);
        initRecycler();

        //Edit button which opens the editor of the information category
        ImageButton edit = categoryLayout.findViewById(R.id.restriction_category_edit);
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
