package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.PDF.Objects.Chapter;
import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.R;

/*
The BasicHeader has a simple design that signifies the current category, system, and page #.
 */

public class CategoryHeaderModule extends Module
{

    private Category category;

    public CategoryHeaderModule(Category category)
    {

        this.category = category;

    }


    @Override
    public void establishHeight() {
        height = 100;
    }

    @Override
    public void establishWidth() {
        width = PAGE_WIDTH;
    }

    @Override
    public View initAndGetViews(Context context)
    {

        View view = LayoutInflater.from(context).inflate(R.layout.module_category_start, null, false);

        TextView categoryHeader = view.findViewById(R.id.module_category_start_title);
            categoryHeader.setText(category.getName());

        TextView categoryDescription = view.findViewById(R.id.module_category_start_description);
            categoryDescription.setText("This is the start of the " + category.getName() + " category in the " + category.getSystem().getDisplayName() + " system.");


        return view;
    }
}
