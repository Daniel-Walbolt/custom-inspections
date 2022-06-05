package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MediaRecycler;

public class RestrictionItem extends CategoryItem
{

    private boolean isGlobal = false;

    public RestrictionItem(String name, Category category) {
        super(name, category);

    }

    public RestrictionItem(String name, Category category, long ID) {
        super(name, category, ID);
    }

    //This method is only called by the CategoryItem Dialog
    public void setGlobal(boolean isGlobal)
    {

        this.isGlobal = isGlobal;

        //TODO Add this restriction to the global restrictions list.

    }

    public boolean isGlobal()
    {

        return isGlobal;

    }

    public void initMedia(RecyclerView mediaRecycler, TextView emptyView)
    {

        new MediaRecycler(this, mediaRecycler, emptyView);

    }

}
