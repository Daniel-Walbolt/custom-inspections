package daniel.walbolt.custominspections.PDF.Objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import daniel.walbolt.custominspections.R;

public class TableMark extends Module
{

    /*
    The TableMark class is used to populate a table of contents.
     */

    private String name;
    private int pageNumber;

    public TableMark(String name, int pageNumber)
    {

        this.name = name;
        this.pageNumber = pageNumber;

    }


    @Override
    public void establishHeight() {
        height = 60;
    }

    @Override
    public void establishWidth() {
        width = 1160;
    }

    @Override
    public View initAndGetViews(Context context)
    {

        View view = LayoutInflater.from(context).inflate(R.layout.module_table_mark, null, false);

        TextView title = view.findViewById(R.id.module_table_mark_title);
            title.setText(name);
        TextView pageNumber = view.findViewById(R.id.module_table_mark_number);
            pageNumber.setText(makeNumber());

        return view;
    }

    //Returns a string that is 3 characters long.
    private String makeNumber()
    {

         if (String.valueOf(pageNumber).length() == 3)
             return String.valueOf(pageNumber);
         else if(String.valueOf(pageNumber).length() == 2)
             return "0" + pageNumber;
         else
             return "00" + pageNumber;
    }

}
