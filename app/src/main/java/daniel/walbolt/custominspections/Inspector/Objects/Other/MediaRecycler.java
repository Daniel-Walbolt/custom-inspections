package daniel.walbolt.custominspections.Inspector.Objects.Other;

import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.rpc.PreconditionFailure;

import daniel.walbolt.custominspections.Adapters.MediaRecyclerAdapter;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;

public class MediaRecycler
{

    private RecyclerView recycler;
    private TextView emptyView;
    private CategoryItem section;

    public MediaRecycler(CategoryItem section, RecyclerView recycler, TextView emptyView) // The media recycler object is a controller for the commonly used media recycler. This class is used to reduce boilerplate code
    {

        this.section = section;
        this.recycler = recycler;
        this.emptyView = emptyView;

        initRecycler();

    }

    private void initRecycler()
    {

        //The RecyclerView should already by initialized
        MediaRecyclerAdapter adapter = new MediaRecyclerAdapter(section.getPictures(), recycler, emptyView, false);
        LinearLayoutManager manager = new LinearLayoutManager(recycler.getContext(), RecyclerView.HORIZONTAL, false);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(manager);
        recycler.setNestedScrollingEnabled(true); // This recycler scrolls horizontally

    }

    public void update()
    {

        recycler.getAdapter().notifyDataSetChanged();

    }

}
