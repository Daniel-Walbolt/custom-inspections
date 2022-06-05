package daniel.walbolt.custominspections.Adapters.PDF;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PDFPageDecoration extends RecyclerView.ItemDecoration
{

    public PDFPageDecoration() { super(); }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {

        //Space out every page from the next page by 20 pixels
        outRect.bottom = 20;

    }

}
