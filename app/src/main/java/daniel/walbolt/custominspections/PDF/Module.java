package daniel.walbolt.custominspections.PDF;

import android.content.Context;
import android.view.View;

public abstract class Module
{

    /*
    A module is any view that takes up space on a page
     */

    public int height = 0;
    public int width = PAGE_WIDTH;

    //The height and width of every page is a constant value. This value is dependent on the type of document we're printing to.
    // For now, every document will be a PDF, standard NA_LETTER.
    public static final int PAGE_HEIGHT = Page.PAGE_HEIGHT;
    public static final int PAGE_WIDTH = Page.PAGE_WIDTH;

    //The height of the module must be accounted for.
    //This method is used basically as an 'event' to trigger the calculation of nested modules.
    //If there is a list module, it will not be able to calculate its final height and width when its first created.
    public abstract void establishHeight();

    //Similarly to establish height, we finalize the width of each module before final width calculations are made for list modules.
    public abstract void establishWidth();

    public int getHeight()
    {

        return height;

    }

    public int getWidth()
    {

        return width;

    }

    public abstract View initAndGetViews(Context context);

}
