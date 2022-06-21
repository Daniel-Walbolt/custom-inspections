package daniel.walbolt.custominspections.PDF;

/*

The PDFController dictates where modules go by using their measurements and the available space in pages.

 */

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.CategoryItem;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Pages.Main;

public class PDFController
{

    //These modules exist within this controller, but are not necessarily on a page.
    private ArrayList<Module> modules;

    private Chapter currentChapter;
    private ArrayList<Chapter> chapters;

    private int height = 0;

    public PDFController()
    {

        modules = new ArrayList<>();
        chapters = new ArrayList<>();

        getSystemModules(); // Get the modules that will display the inspection information, this is computationally intensive.

    }

    /*
    The height is required of any module object.

    The height of the modules and the constant height of a page allows for calculations of total pages.
     */
    private void establishHeight()
    {

        //Find every module and add it to the existing height value.
        for(Module module : modules)
        {

            height += module.height;

        }

    }

    //Put all the system information into modules that go into the PDF
    public void getSystemModules()
    {

        for(System system : Main.inspectionSchedule.inspection.getSystemList())
        {

            //Every system is a new chapter.
            Chapter systemChapter = new Chapter(system.getDisplayName());

            //Loop through every system in the inspection.
            for(Category category : system.getCategories())
            {

                //Loop through every category in every system.

                for(CategoryItem item : category.getCategoryItems())
                {

                    //Loop through every item in every system.
                    //Sort the items into modules that will display their information.

                }

            }

        }

    }

    //Assigning modules to their pages according to height
   /* public void transferModulesToPages(ArrayList<Page> pages)
    {

        int currentPage = 0;
        int currentPageViewHeight = pages.get(currentPage).getOccupiedHeight();

        for(Module module : modules)
        {

            if(module != null)
            {


                //Every module is put into a page
                //Check if adding this module will go beyond the page's boundaries.
                if(currentPageViewHeight + module.getHeight() > Page.PAGE_HEIGHT)
                {

                    //Reset the current height to the beginning of a page.
                    currentPageViewHeight = module.getHeight();
                    currentPage++; //Choose the next page to initialize views to.
                    if(currentPage >= pages.size())
                        break;
                    else
                        pages.get(currentPage).addModule(module); // Add the module to the next page

                }
                else
                {

                    //Add the module to the current page and account for its height.
                    Page page = pages.get(currentPage);
                    currentPageViewHeight += module.getHeight();
                    page.addModule(module);

                }

                pages.get(currentPage).setOccupiedHeight(currentPageViewHeight);

            }

        }*/



    public boolean removeModule(Module module)
    {

        return modules.remove(module);

    }

}
