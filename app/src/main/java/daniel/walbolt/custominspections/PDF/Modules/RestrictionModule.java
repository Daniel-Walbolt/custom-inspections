package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.ObservationItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.RestrictionItem;
import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.R;

public class RestrictionModule extends Module
{

    /*
    The RestrictionModule handles the display of Restrictions on the Inspection. Layout is very similar to Defects and Observations
     */

    private RestrictionItem restriction;
    private ArrayList<CommentModule> comments;
    private boolean hasImages = true; // Should this module display the "Images" textview?

    public RestrictionModule(Context context, RestrictionItem restriction)
    {

        this.restriction = restriction;
        this.comments = new ArrayList<>();

        ArrayList<String> comments = new ArrayList<>(restriction.getCommentMedia().getComments());

        if (comments.isEmpty())
            comments.add("There is no comment about this restriction."); // Default comment when there are no comments

        for (int i = 0; i < comments.size(); i++)
        {

            String comment = comments.get(i);
            CommentModule commentModule = new CommentModule(context, comment, i+1);
            this.comments.add(commentModule);

        }

    }

    @Override
    public void establishHeight()
    {
        this.height = 200;
        for (CommentModule comment : comments)
        {

            comment.establishHeight();
            this.height += comment.getHeight();

        }

    }

    @Override
    public void establishWidth() {
        this.width = 1200;
    }

    @Override
    public View initAndGetViews(Context context)
    {

        View v = LayoutInflater.from(context).inflate(R.layout.module_restriction, null, false);

        TextView title = v.findViewById(R.id.module_restriction_title);
            StringBuilder restrictionTitle = new StringBuilder();
            restrictionTitle.append(restriction.getName());
            if (restriction.getGroup() != null)
                restrictionTitle.append(" - " + restriction.getGroup().getName());
            else
                restrictionTitle.append(" - General");
            title.setText(restrictionTitle.toString());

        TextView imagesText = v.findViewById(R.id.module_restriction_imageText);
            imagesText.setVisibility(hasImages ? View.VISIBLE : View.INVISIBLE);

        LinearLayout commentsContainer = v.findViewById(R.id.module_restriction_comments);

        //Comment list is never empty.
        for(CommentModule comment : comments)
            commentsContainer.addView(comment.initAndGetViews(context));

        return v;

    }

    public void setHasImages(boolean hasImages)
    {

        this.hasImages = hasImages;

    }

}
