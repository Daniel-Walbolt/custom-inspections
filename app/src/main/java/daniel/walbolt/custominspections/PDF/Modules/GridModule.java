package daniel.walbolt.custominspections.PDF.Modules;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.PDF.Objects.Page;
import daniel.walbolt.custominspections.R;

/*

The GridModule uses horizontal and vertical measurements to layout items efficiently as possible.

 */

public class GridModule extends Module
{

    private ArrayList<ArrayList<? extends Module>> rows;
    private ArrayList<Module> overflowModules;
    private final int HEIGHT_LIMIT;
    private int longestRow = 0;

    public GridModule(ArrayList<? extends Module> modules, int HEIGHT_LIMIT)
    {

        this.HEIGHT_LIMIT = HEIGHT_LIMIT;
        rows = new ArrayList<>();
        overflowModules = new ArrayList<>();
        assignModules(modules);

    }

    @Override
    public void establishHeight()
    {

        //The height of the grid is the sum of all rows.

    }

    @Override
    public void establishWidth()
    {

        //The width of the grid is the width of the longest row.


    }

    //Returns the modules that can't fit on the page because the grid is larger than the page view.
    public ArrayList<Module> getOverflowModules()
    {

        return overflowModules;

    }

    @Override
    public View initAndGetViews(Context context)
    {

        System.out.println("Initializing grid module with height: " + this.height);
        
        View view = LayoutInflater.from(context).inflate(R.layout.module_grid, null, false);

        LinearLayout column = view.findViewById(R.id.module_grid_column); // This is probably the same as "View" right above.

        //Loop through the list of rows
        for (ArrayList<? extends Module> row : rows)
        {

            //Create the row
            LinearLayout rowLayout = new LinearLayout(context);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            //Every module has already been measured, loop through the assigned modules in this row
            for(Module module : row)
            {

                //Initialize the views of the module and add them to the row layout.
                rowLayout.addView(module.initAndGetViews(context));

            }

            //Add each row to the single column.
            column.addView(rowLayout);

            SpacerModule rowSpacer = new SpacerModule(50);
            column.addView(rowSpacer.initAndGetViews(context));

        }

        return view;

    }


    //Create the rows with the given list of modules.
    private void assignModules(ArrayList<? extends Module> unorganizedModules)
    {

        int currentWidth = -1; // This is the current width of the target row.
        int rowHeight = 0; // This value stores the tallest module in a row.
        ArrayList<Module> row = new ArrayList<>();

        boolean isOverflow = false; // This value determines if the remaining modules being iterated won't fit on the page.

        for (Module module : unorganizedModules) // Loop through every module.
        {

            module.establishWidth(); // Finalize the width of the target module
            module.establishHeight(); // Finalize the height of the target module

            //Check if the module we're considering is being placed outside the available space on the page
            if (isOverflow)
            {

                overflowModules.add(module);
                continue;

            }

            //If the currentWidth is -1, start a new row. Or if adding the module surpasses the width of the page, create a new row.
            if (currentWidth == -1 || (currentWidth + module.getWidth() > PAGE_WIDTH))
            {
                
                //It's important to note that if a module is added that is beyond the width of a page, it will be inserted into an empty row, and will go out of bounds of the page.
                //This behavior is favored over just removing the module from the list all together.
                if (currentWidth + module.getWidth() > PAGE_WIDTH)
                {

                    rows.add(row); // The row is full, add it to the final row list.

                    this.height += rowHeight; // Add the height of the full row to the total height of the grid
                    this.height += 50; // Add 50 more pixels to the height, because rows have spacing in between them.

                    //If the old row is the longest row, record its length.
                    if (currentWidth > longestRow)
                        longestRow = currentWidth;

                }
                row = new ArrayList<>(); // Create the new row
                
                row.add(module);

                //If this module is the tallest module in this row, record its value
                if (module.getHeight() > rowHeight)
                    rowHeight = module.getHeight();

                //The current width of the row is now the width of the first module in it.
                currentWidth = module.getWidth();

            }
            else // The module fits in the current row
            {

                row.add(module);

                //If this module is the tallest module in this row, record its value
                if (module.getHeight() > rowHeight) {
                    rowHeight = module.getHeight();
                }
                currentWidth += module.getWidth();

            }

            //The module has been added to a row, check if the grid itself is taller than a page.
            //The height of the module is only updated after a row is filled, or the last item has been added.
            //But whenever we add a new module to a row, there is a chance that that module is bigger than the other modules in that row.
            //Each module's height must be tested to see if it extends the grid module's height beyond its defined limit.
            if ((this.height + module.getHeight()) > HEIGHT_LIMIT)
            {

                //The module that was added has exceed the height limit of the grid
                //Remove the row that exceeded the limit
                row = null;
                rowHeight = 0;

                //Add the module that was added to the overflow modules
                overflowModules.add(module);

                //Mark the rest of the modules in the loop as overflow.
                isOverflow = true;

            }

        }

        if (row != null)
        {

            rows.add(row); //Add the last row in the grid. Rows are only added once they are full otherwise.
            this.height += rowHeight + 50; // Add the last row's height to the total height of the grid and also add the 50px spacer after each row.


        }

    }
}
