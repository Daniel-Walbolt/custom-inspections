package daniel.walbolt.custominspections.Adapters;

import android.content.Intent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Activities.SystemActivity;
import daniel.walbolt.custominspections.Inspector.Objects.Inspection;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.R;

public class InspectionSystemAdapter extends RecyclerView.Adapter<InspectionSystemAdapter.SystemHolder>
{

    private ArrayList<System> systems;
    private Inspection inspection;
    private ItemTouchHelper mTouchHelper;
    private boolean isSubSystems = false;

    private int lastPosition = -1;

    public InspectionSystemAdapter(final RecyclerView thisRecyclerView, final TextView thisEmptyView, ArrayList<System> systems, boolean isSubSystems)
    {

        this.inspection = Main.inspectionSchedule.inspection;
        this.systems = systems;
        this.isSubSystems = isSubSystems;

        //If the systems provided are empty, then display the empty view.
        // This view basically informs the user there is nothing to display here.
        if(systems.isEmpty())
        {

            thisRecyclerView.setVisibility(View.GONE);
            thisEmptyView.setVisibility(View.VISIBLE);

        }
        else
        {

            thisRecyclerView.setVisibility(View.VISIBLE);
            thisEmptyView.setVisibility(View.GONE);

        }


        //Create a data observer to update the visibility of the empty view if the data becomes empty, or not empty.
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged()
            {

                if(getItemCount() == 0)
                {

                    thisRecyclerView.setVisibility(View.GONE);
                    thisEmptyView.setVisibility(View.VISIBLE);

                }
                else
                {

                    thisRecyclerView.setVisibility(View.VISIBLE);
                    thisEmptyView.setVisibility(View.GONE);

                }

            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {

                if(getItemCount() == 0)
                {

                    thisRecyclerView.setVisibility(View.GONE);
                    thisEmptyView.setVisibility(View.VISIBLE);

                }
                else
                {

                    thisRecyclerView.setVisibility(View.VISIBLE);
                    thisEmptyView.setVisibility(View.GONE);

                }

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount)
            {

                if(getItemCount() == 0)
                {

                    thisRecyclerView.setVisibility(View.GONE);
                    thisEmptyView.setVisibility(View.VISIBLE);

                }
                else
                {

                    thisRecyclerView.setVisibility(View.VISIBLE);
                    thisEmptyView.setVisibility(View.GONE);

                }
            }
        });

    }

    //Retrieve and inflate the view that holds one of the ArrayList items.
    @NonNull
    @Override
    public SystemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.system_recycler, parent, false);

        return new SystemHolder(view);

    }

    //Bind data to the view by finding internal views with IDs
    @Override
    public void onBindViewHolder(@NonNull SystemHolder holder, int position)
    {

        System system = systems.get(position);

        holder.title.setText(system.getDisplayName());
        holder.mediaCount.setText(String.valueOf(system.getMediaCount()));
        holder.commentCount.setText(String.valueOf(system.getCommentCount()));
        holder.tags.setAdapter(new SystemTagRecyclerAdapter(system));
        holder.tags.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.tags.setNestedScrollingEnabled(false);

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.scale_up);
        holder.itemView.setAnimation(animation);
        lastPosition = position;

    }

    @Override
    public int getItemCount()
    {

        return systems.size();

    }

    public class SystemHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        public TextView title;
        public TextView mediaCount;
        public TextView commentCount;

        public RelativeLayout background;
        public LinearLayout foreground;

        public RecyclerView tags;

        GestureDetector mGestureDetector;

        public SystemHolder(@NonNull View itemView)
        {

            super(itemView);
            title = itemView.findViewById(R.id.recycler_system_title);
            tags = itemView.findViewById(R.id.recycler_system_tags);
            mediaCount = itemView.findViewById(R.id.recycler_system_media);
            commentCount = itemView.findViewById(R.id.recycler_system_comments);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v)
        {

            //When the system is clicked on, open a new activity. (Visual effect which also gives back button functionality on android).
            Intent openSystem = new Intent(itemView.getContext(), SystemActivity.class);

            //Is the system being opened a subsytem?
            openSystem.putExtra("isSubSystem", isSubSystems);

            //If it is a subsystem, send the parent system
            if(isSubSystems)
                openSystem.putExtra("ParentSystem", systems.get(getAdapterPosition()).getParentSystem().getDisplayName());

            //Send the name of the system itself whether it is a subsystem or not.
            openSystem.putExtra("SystemName", systems.get(getAdapterPosition()).getDisplayName());
            itemView.getContext().startActivity(openSystem);

        }
    }

}

