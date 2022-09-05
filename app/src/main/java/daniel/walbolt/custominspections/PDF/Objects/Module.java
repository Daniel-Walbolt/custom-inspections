package daniel.walbolt.custominspections.PDF.Objects;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import daniel.walbolt.custominspections.PDF.Objects.Page;

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

    //In order to increase versatility, each module stores the reference to the page number it is on.
    public int pageNumber = -1; // This number should only be edited by the PDFController

    public int getHeight()
    {

        return height;

    }

    public int getWidth()
    {

        return width;

    }

    public abstract View initAndGetViews(Context context);

    public int calculateTextViewHeight(Context context, String text, int maxWidth)
    {

        //Create a mock textview that is as similar as possible to the real comment.
        TextView textView = new TextView(context);
        textView.setTypeface(Typeface.DEFAULT);
        textView.setText(text, TextView.BufferType.NORMAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);

        int widthMeasuredSpec = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        int heightMeasuredSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        textView.measure(widthMeasuredSpec, heightMeasuredSpec);

        return textView.getMeasuredHeight();

    }

}
