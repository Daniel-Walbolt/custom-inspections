package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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

    public void addItem(CategoryItem item)
    {

        //Add the Category Item to the list. For the information category, this is a simple list of category items.
        categoryItems.add(item);

    }

    @Override
    public void load(LinearLayout pageLayout)
    {

        //Get the layout of an information category
        View categoryLayout = LayoutInflater.from(pageLayout.getContext()).inflate(R.layout.information_category, pageLayout, false);

        //Initialize the necessary views inside the category, including the Category Items
        LinearLayout categoryItemsContainer = categoryLayout.findViewById(R.id.information_category_items);

        //Edit button which opens the editor of the information category
        ImageButton edit = categoryLayout.findViewById(R.id.information_category_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CategoryDialog(pageLayout.getContext(), thisClass());
            }
        });

        //TODO Load the children views

        //Add the entire category to the page
        pageLayout.addView(categoryLayout);

    }


}
