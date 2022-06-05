package daniel.walbolt.custominspections.PDF.Modules;


import android.content.Context;
import android.database.CrossProcessCursorWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import daniel.walbolt.custominspections.PDF.Module;
import daniel.walbolt.custominspections.R;

/*

The GridModule uses horizontal and vertical measurements to layout items efficiently as possible.

 */

public class GridModule extends Module
{

    private ArrayList<ArrayList<Module>> rows;
    private int longestRow = 0;
    private int totalHeight = 0;

    public GridModule(ArrayList<Module> modules)
    {

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

    @Override
    public View initAndGetViews(Context context)
    {

        View view = LayoutInflater.from(context).inflate(R.layout.module_grid, null, false);

        LinearLayout column = view.findViewById(R.id.module_grid_column); // This is probably the same as "View" right above.

        //Loop through the list of rows
        for (ArrayList<Module> row : rows)
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

        }

        return view;

    }


    //Create the rows with the given list of modules.
    private void assignModules(ArrayList<Module> unorganizedModules)
    {

        int currentWidth = -1; // This is the current width of the target row.
        int rowHeight = 0; // This value stores the tallest module in a row.
        ArrayList<Module> row = new ArrayList<>();
        for (Module module : unorganizedModules) // Loop through every module.
        {

            module.establishWidth(); // Finalize the width of the target module
            module.establishHeight(); // Finalize the height of the target module
            
            //If the currentWidth is -1, start a new row. If adding the module surpasses the width of the page, create a new row.
            if (currentWidth == -1 || (currentWidth + module.getWidth() > PAGE_WIDTH))
            {
                
                //It's important to note that if a module is added that is beyond the width of a page, it will be inserted into an empty row, and will go out of bounds of the page.
                //This behavior is favored over just removing the module from the list all together.
                if (currentWidth + module.getWidth() > PAGE_WIDTH)
                {

                    rows.add(row); // The row is full, add it to the final row list.

                    //If this row is the longest row, record its length.
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
                if (module.getHeight() > rowHeight)
                    rowHeight = module.getHeight();

                currentWidth += module.getWidth();

            }


        }

    }
}
