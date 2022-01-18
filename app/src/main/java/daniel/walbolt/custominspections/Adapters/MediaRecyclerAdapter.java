package daniel.walbolt.custominspections.Adapters;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CommentDialog;
import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.ImageDialog;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.R;

public class MediaRecyclerAdapter extends RecyclerView.Adapter<MediaRecyclerAdapter.MediaHolder> implements ItemTouchHelperAdapter
{

    private ArrayList<InspectionMedia> media;

    private boolean hasComments = true;

    public MediaRecyclerAdapter(ArrayList<InspectionMedia> media, final RecyclerView thisRecyclerView, final TextView thisEmptyView)
    {

        this.media = media;

        if(!media.isEmpty())
        {

            thisRecyclerView.setVisibility(View.VISIBLE);
            thisEmptyView.setVisibility(View.GONE);

        }

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

    public MediaRecyclerAdapter(ArrayList<InspectionMedia> media, final RecyclerView thisRecyclerView, final TextView thisEmptyView, boolean hasComments)
    {

        this(media, thisRecyclerView, thisEmptyView);
        this.hasComments = hasComments;

    }

    @NonNull
    @Override
    public MediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_recycler, parent, false);

        return new MediaHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MediaHolder holder, int position)
    {

        System.out.println(media.get(position).getFileName());

        if(media.get(position).getFile() != null)
            holder.mImageView.setImageURI(media.get(position).getURI(holder.itemView.getContext()));

    }

    @Override
    public int getItemCount() {
        return media.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition)
    {

        InspectionMedia fromImage = media.get(fromPosition);
        media.remove(fromPosition);

        media.add(toPosition, fromImage);
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onItemSwiped(int position)
    {

    }

    class MediaHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        //Image
        public ImageView mImageView;

        public ImageButton comment;

        //Background
        public RelativeLayout background;

        public MediaHolder(@NonNull View view)
        {

            super(view);

            mImageView = view.findViewById(R.id.recycler_image);
            background = view.findViewById(R.id.recycler_image_background);

            ImageButton remove = view.findViewById(R.id.recycler_image_remove);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    media.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });

            comment = view.findViewById(R.id.recycler_image_comment);
            if(!hasComments)
            {

                comment.setVisibility(View.INVISIBLE);

            }
            else
                comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CommentDialog(itemView.getContext(), media.get(getAdapterPosition()));
                    }
                });

            view.setOnClickListener(this);

        }

        private long firstTap = 0;


        @Override
        public void onClick(View v) {
            if(firstTap == 0)
                firstTap = System.currentTimeMillis();
            else if(System.currentTimeMillis() - firstTap < 500) // The view was double-tapped
            {

                new ImageDialog(itemView.getContext(), media.get(getAdapterPosition()));
                firstTap = 0;

            }
            else // If the tap was not within half a second, reset.
                firstTap = 0;

        }
    }

}
