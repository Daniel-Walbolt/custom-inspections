package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.R;

public class CommentModule extends Module
{

    /*
    The CommentModule take text and displays it.
    When displaying to a PDF, it is incredibly important that we keep track of how much space is left on a page in order to prevent clipping views.
    TextViews make this difficult when they have variable text length. This module provides functionality to predict the height of a textview before rendering it.
     */

    private String text;
    private int index; // Index displays the comment number on the comment. This may seem meaningless, but it may help with finding a comment a customer asks about.
    private int textViewHeight;

    public CommentModule(Context context, String comment, int index )
    {

        this.text = comment;
        this.index = index;

        textViewHeight = calculateTextViewHeight(context, text, 1000);

    }


    @Override
    public void establishHeight()
    {

        this.height = 10 + textViewHeight; // Bottom padding of the layout

    }

    @Override
    public void establishWidth() {

    }

    @Override
    public View initAndGetViews(Context context)
    {

        View v = LayoutInflater.from(context).inflate(R.layout.module_comment, null, false);

        TextView index = v.findViewById(R.id.module_comment_index);
            index.setText(Integer.toString(this.index));
        TextView comment = v.findViewById(R.id.module_comment_text);
            comment.setText(text);

        return v;

    }

}
