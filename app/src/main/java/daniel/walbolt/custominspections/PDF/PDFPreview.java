package daniel.walbolt.custominspections.PDF;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.PDF.PDFPageDecoration;
import daniel.walbolt.custominspections.Adapters.PDF.PDFPageRecyclerAdapter;
import daniel.walbolt.custominspections.PDF.Objects.Page;
import daniel.walbolt.custominspections.R;

public class PDFPreview
{

    private NestedScrollView pdfContainer;
    private RecyclerView pageRecycler;
    private PDFPageRecyclerAdapter pdfRecyclerAdapter;
    private ArrayList<Page> pages;

    /*
    The PDFPreview class starts the PDF rendering process.

    Opens the PDF container page

    Initialize the buttons on the page (upload button)

    Establish the size of the pages through wizardry

    Create the PDFController

    Interacting with the generate button will initiate the PDFAssembler which takes the generated pages from the Controller and puts them into a pdf file.

     */

    public PDFPreview(Activity activity)
    {

        //Display the PDF Preview page
        activity.setContentView(R.layout.pdf_viewer);

        //Initialize the views of the preview
        initViews(activity);

    }

    private void initViews(final Activity activity)
    {

        pdfContainer = activity.findViewById(R.id.pdf_scrollView);
        pageRecycler = pdfContainer.findViewById(R.id.pdf_recycler); // The Recycler View

        getPages(activity);

        //The generate button finalizes the drawn PDF and turns it into a real PDF file.
        final Button generate = activity.findViewById(R.id.pdf_generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishPDF(generate.getContext());
            }
        });

    }

    private void finishPDF(Context context)
    {

        PDFAssembler generator = new PDFAssembler(context, this, pages);
        generator.printPDF();

    }

    public void getPages(Activity activity)
    {

        //Create the PDFController. This class filters the inspection information and displays it properly.
        PDFController controller = new PDFController();
        pages = controller.generatePages(activity);

        PDFPageRecyclerAdapter PDFAdapter = new PDFPageRecyclerAdapter(pages);

        LinearLayoutManager manager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);


        pageRecycler.setLayoutManager(manager);
        pageRecycler.setNestedScrollingEnabled(false);
        pageRecycler.addItemDecoration(new PDFPageDecoration());
        pageRecycler.setAdapter(PDFAdapter);

    }

    public int getPageCount()
    {

        return pages.size();

    }

}
