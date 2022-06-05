package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MediaRecycler;

public class ObservationItem extends CategoryItem
{

    public ObservationItem(String name, Category category) {
        super(name, category);
    }

    public ObservationItem(String name, Category category, long ID) {
        super(name, category, ID);
    }

    public void initMedia(RecyclerView mediaRecycler, TextView emptyView)
    {

        new MediaRecycler(this, mediaRecycler, emptyView);

    }

}
