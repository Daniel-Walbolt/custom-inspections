package daniel.walbolt.custominspections.Inspector.Objects.Categories;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.InspectionSystemAdapter;
import daniel.walbolt.custominspections.Inspector.Dialogs.Creators.SystemsDialog;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class Sub_System extends Category{

    public Sub_System(System parent) {
        super(TYPE.SUB_SYSTEMS, parent);
    }

    @Override
    public void loadToPage(LinearLayout pageLayout)
    {

        //Get the layout of an information category
        View categoryLayout = LayoutInflater.from(pageLayout.getContext()).inflate(R.layout.subsystems_category, pageLayout, false);

        //Initialize the necessary views inside the category, including the Category Items
        categoryRecycler = categoryLayout.findViewById(R.id.subsystems_category_items);
        emptyView = categoryLayout.findViewById(R.id.sub_system_category_emptyView);
        initRecycler();

        //Edit button which opens the editor of the information category
        ImageButton add = categoryLayout.findViewById(R.id.subsystems_category_edit);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SystemsDialog(pageLayout.getContext(), getSystem().getSubSystems(), getSystem()).setOnDismissListener(
                        dialog -> categoryRecycler.getAdapter().notifyDataSetChanged()
                );
            }
        });

        //Add the entire category to the page
        pageLayout.addView(categoryLayout);

    }

    @Override
    public void initRecycler()
    {

        InspectionSystemAdapter adapter = new InspectionSystemAdapter(categoryRecycler, emptyView, getSystem().getSubSystems(), true );
        LinearLayoutManager manager = new LinearLayoutManager(categoryRecycler.getContext(), RecyclerView.VERTICAL, false);
        categoryRecycler.setAdapter(adapter);
        categoryRecycler.setLayoutManager(manager);
        categoryRecycler.setNestedScrollingEnabled(false);

    }

    @Override
    public ArrayList<CategoryItem> getCategoryItems()
    {

        //This category has no category items
        return new ArrayList<>();

    }

}
