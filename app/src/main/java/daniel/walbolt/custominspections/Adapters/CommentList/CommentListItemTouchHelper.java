package daniel.walbolt.custominspections.Adapters.CommentList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import daniel.walbolt.custominspections.Adapters.ItemTouchHelperAdapter;
import daniel.walbolt.custominspections.R;

public class CommentListItemTouchHelper extends ItemTouchHelper.Callback
{

    private final ItemTouchHelperAdapter mAdapter;

    public CommentListItemTouchHelper(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        ((CommentListRecyclerAdapter.CommentHolder)viewHolder).foreground.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.white));

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG)
        {

            ((CommentListRecyclerAdapter.CommentHolder)viewHolder).foreground.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.dialog_orange_border));

        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
    {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlag = ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
    {

        mAdapter.onItemSwiped(viewHolder.getAdapterPosition());

    }

}
