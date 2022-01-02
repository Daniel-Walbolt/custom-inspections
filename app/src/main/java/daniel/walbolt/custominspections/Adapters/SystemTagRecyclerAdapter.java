package daniel.walbolt.custominspections.Adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import daniel.walbolt.custominspections.Constants.SystemTags;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class SystemTagRecyclerAdapter extends RecyclerView.Adapter<SystemTagRecyclerAdapter.SystemTagHolder>
{

    private System targetSystem;
    private boolean forPDF = false;

    public SystemTagRecyclerAdapter(System targetSystem)
    {

        this.targetSystem = targetSystem;

    }

    public void setForPDF(boolean value)
    {

        forPDF = value;

    }

    @NonNull
    @Override
    public SystemTagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        return new SystemTagHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_system_tag, parent, false));
            //return new SystemTagHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_module_system_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SystemTagHolder holder, int position)
    {

        SystemTags tag = targetSystem.getStatus().get(position);

        Drawable tagColor = ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.system_tag_background); //Get the layout for a tag
        tagColor.setTint(tag.getTagColor(holder.itemView.getContext())); // Set the custom color
        tagColor.setAlpha(127);

        holder.background.setBackground(tagColor); // Set the layout to the recycler item
        if(!forPDF)
            holder.title.setText(tag.toString());
        else
            holder.title.setText(tag.getPDFString());

    }

    @Override
    public int getItemCount() {
        return targetSystem.getStatus().size();
    }

    class SystemTagHolder extends RecyclerView.ViewHolder
    {

        public RelativeLayout background;
        public TextView title;

        public SystemTagHolder(@NonNull View itemView)
        {
            super(itemView);

            background = itemView.findViewById(R.id.recycler_systemtag_background); // Find the background layout
            title = itemView.findViewById(R.id.recycler_systemtag_title); // Find the text view for the name of the SystemTag

        }
    }

}

