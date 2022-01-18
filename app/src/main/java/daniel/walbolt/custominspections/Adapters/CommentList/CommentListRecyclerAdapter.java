package daniel.walbolt.custominspections.Adapters.CommentList;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.ItemTouchHelperAdapter;
import daniel.walbolt.custominspections.Inspector.Dialogs.Editors.CommentDialog;
import daniel.walbolt.custominspections.R;

public class CommentListRecyclerAdapter extends RecyclerView.Adapter<CommentListRecyclerAdapter.CommentHolder> implements ItemTouchHelperAdapter
{

    private ArrayList<String> comments;
    private ItemTouchHelper mTouchHelper;
    private boolean isSavedComments;

    private CommentDialog mCommentDialog;

    private Context mContext;

    private long firstTouchTime = 0;

    public CommentListRecyclerAdapter(Context context, ArrayList<String> comments, final CommentDialog commentDialog, final boolean isSavedComments, final RecyclerView mRecyclerView, final TextView emptyView)
    {

        this.comments = comments;
        this.mContext = context;
        this.mCommentDialog = commentDialog;
        this.isSavedComments = isSavedComments;

        //If there are no comments, display the message indicating how to add them
        if(comments.isEmpty())
        {

            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);

        }
        else
        {

            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

        }

        //While the previous if statement works on initialization, this even handler checks for no comments whenever the list changes
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged()
            {

                if(getItemCount() == 0)
                {

                    mRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);

                }
                else
                {

                    mRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);

                }

            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {

                if(getItemCount() == 0)
                {

                    mRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);

                }
                else
                {

                    mRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);

                }

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount)
            {

                if(getItemCount() == 0)
                {

                    mRecyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);

                }
                else
                {

                    mRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);

                }
            }
        });

    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_recycler, parent, false);

        return new CommentListRecyclerAdapter.CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentHolder holder, int position)
    {

        holder.mText.setText(comments.get(position));
        holder.mText.setTextColor(Color.parseColor("#000000"));

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition)
    {
        String comment = comments.get(fromPosition);
        comments.set(fromPosition, comments.get(toPosition));
        comments.set(toPosition, comment);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int position)
    {

        //If the user is EDITING a comment
        if(mCommentDialog.isEditing)
        {

            //If the comment being edited is a saved comment, call a method for editing a saved comment
            if(isSavedComments)
                mCommentDialog.editSavedComment(comments.get(position));
            else
                mCommentDialog.editComment(comments.get(position));
            return;

        }

        //If an item is swiped when NOT EDITING a comment, delete it.
        if(isSavedComments)
        {

            mCommentDialog.removeSavedComment(comments.get(position));
            Toast.makeText(mCommentDialog.getContext(), "Removed Saved Comment", Toast.LENGTH_SHORT).show();

        }
        else
        {

            Toast.makeText(mCommentDialog.getContext(), "Removed Comment", Toast.LENGTH_SHORT).show();
            comments.remove(position);

        }
        notifyItemRemoved(position);

    }

    //When a saved comment is double tapped, append the comment to the new comment.
    void appendComment(String comment)
    {

        mCommentDialog.appendComment(comment);

    }

    public void setTouchHelper(ItemTouchHelper helper)
    {

        this.mTouchHelper = helper;

    }

    class CommentHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener
    {

        //The comment
        TextView mText;

        RelativeLayout foreground;

        GestureDetector mGestureDetector;

        public CommentHolder(View itemView)
        {

            super(itemView);

            mText = itemView.findViewById(R.id.recycler_dialog_comment_text);
            foreground = itemView.findViewById(R.id.recycler_dialog_comment_foreground);

            mGestureDetector = new GestureDetector(itemView.getContext(), this);
            itemView.setOnTouchListener(this);

        }


        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {

            if(isSavedComments)
            {

                if(firstTouchTime == 0)
                {

                    firstTouchTime = System.currentTimeMillis();

                }
                else
                {

                    if(System.currentTimeMillis() - firstTouchTime < 750)
                    {

                        appendComment(mText.getText().toString());
                        firstTouchTime = 0;

                    }
                    else
                        firstTouchTime = 0;

                }

            }

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            mTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {

            mGestureDetector.onTouchEvent(event);
            return true;
        }
    }

}
