package daniel.walbolt.custominspections.Adapters.ScheduleRecycler;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InspectionScheduleItemDecorator extends RecyclerView.ItemDecoration
{

    public InspectionScheduleItemDecorator()
    {
        super();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        outRect.bottom = 20;

    }

}
