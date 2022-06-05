package daniel.walbolt.custominspections.PDF;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.PDF.PDFPageDecoration;
import daniel.walbolt.custominspections.R;

public class PDFPreview
{

    private NestedScrollView pdfContainer;
    private RecyclerView pages;
    //private PDFPageRecyclerAdapter pdfRecyclerAdapter;

    public PDFPreview(Activity activity)
    {

        activity.setContentView(R.layout.pdf_viewer);

        initViews(activity);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point smallestSize = new Point();
        Point largestSize = new Point();
        display.getCurrentSizeRange(smallestSize, largestSize);
        final int width = smallestSize.x;
        final int height = smallestSize.y;

        final ViewTreeObserver observer= pages.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                System.out.println("Pages dimensions : " + pages.getWidth() +  ", " + pages.getHeight());

                if(pages.getWidth() != 0 && pages.getHeight() != 0)
                {

                    double scaleX = (double) width/pages.getWidth();

                    pages.setScaleX((float)scaleX-0.3f);
                    pages.setScaleY((float)scaleX-0.3f);

                }
            }
        });

        pages.setMinimumHeight(height);

    }

    private void initViews(final Activity activity)
    {

        pages = pdfContainer.findViewById(R.id.pdf_recycler); // The Recycler View

        //pdfRecyclerAdapter = new PDFPageRecyclerAdapter(pages);
        LinearLayoutManager manager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
       // pages.setAdapter(pdfRecyclerAdapter);
        pages.setLayoutManager(manager);
        pages.setNestedScrollingEnabled(false);
        pages.addItemDecoration(new PDFPageDecoration());

        final Button generate = activity.findViewById(R.id.pdf_generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //generatePDF(activity, adapter);
            }
        });

    }

    /*private void generatePDF(Activity activity, PDFPageRecyclerAdapter adapter)
    {

        PDFAssembler generator = new PDFAssembler(activity,  this);
        generator.generatePDF(activity, adapter);

    }*/

    public ArrayList<Page> getPages()
    {

        //The adapter is created when initializing this class
        return new ArrayList<>();

    }

    public int getPageCount()
    {

        //TODO: Get Page Count
        return 1;

    }

}
