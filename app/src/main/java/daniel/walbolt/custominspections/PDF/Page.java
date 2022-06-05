package daniel.walbolt.custominspections.PDF;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.R;

public class Page {

    /*

        The page class represents the entirely of a single PDF page.
        Every page is a linear layout with measured out "Modules" that make up the information of the inspection.

        The PDFAssembler turns the collection of all Pages into a PDF
        The PDFController delegates the information from the inspection into the contents of these Pages.

     */


    //The pageView is the view given by the PDFRecyclerAdapter when loading
    private View pageView;

    //The pageContent is located on the pageView in order to create a top down left to right order.
    private LinearLayout pageContent;

    //Width and Height in pixels of the page content area.
    static final int PAGE_HEIGHT = 1480;
    static final int PAGE_WIDTH = 1234;

    // Every page has a number assigned to it as it is loaded.
    private final int pageNumber;

    //Every page has a header that contains some basic information
    private String headerChapter; // The name of the chapter this page belongs to

    //In order to maximize the amount of content we can fit onto a page, we keep track of the vertical space in pixels left on the page.
    private int occupiedHeight;

    //As the PDFController assigns Modules to pages based off their available space, it adds the Module objects to the Page.
    //This list is later used to load all of the modules onto the page layout.
    private ArrayList<Module> modules;

    //New pages are only created when delegating modules. If a module does not fit onto the current page, a new page is created.
    public Page(String headerName, int pageNumber)
    {

        modules = new ArrayList<>();
        this.pageNumber = pageNumber;

    }

    //The PDFPreview class creates a RecyclerView that calls this method after loading ViewHolders
    public void attachPageView(View page)
    {

        this.pageView = page;
        this.pageContent = page.findViewById(R.id.pdf_page_content);

        TextView number = page.findViewById(R.id.pdf_page_number);
        String numberText = String.valueOf(pageNumber);
        if (numberText.length() < 3)
            numberText = numberText.length() == 1 ? "00" + pageNumber : "0" + pageNumber;
        number.setText(numberText);

    }

    public View getView()
    {

        return pageView;

    }

    public int getOccupiedHeight()
    {

        return occupiedHeight;

    }

    public void setOccupiedHeight(int newHeight)
    {

        this.occupiedHeight = newHeight;

    }

    public int getPageNumber()
    {

        return pageNumber;

    }

    public void initModules()
    {

        for (Module module : modules) {

            View moduleView = module.initAndGetViews(pageContent.getContext());
            if (moduleView != null)
                pageContent.addView(moduleView);

        }


    }

    void addModule(Module... modules)
    {

        for (Module module : modules) {

            //If the module being added is not null (just a precaution)
            if (module != null) {

                //Add the module to the module list in this chapter
                this.modules.add(module);

            }

        }

    }

}
