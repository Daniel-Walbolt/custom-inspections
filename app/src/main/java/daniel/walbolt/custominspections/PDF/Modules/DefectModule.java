package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.DefectItem;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.RestrictionItem;
import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.R;

public class DefectModule extends Module
{

    /*
    The DefectModule handles the display of Defects of the residence.
     */

    private DefectItem defect;
    private ArrayList<CommentModule> comments;
    private boolean hasImages = true; // Should this module display the "Images" textview?

    public DefectModule(Context context, DefectItem defect)
    {

        this.defect = defect;
        this.comments = new ArrayList<>();

        ArrayList<String> comments = new ArrayList<>(defect.getCommentMedia().getComments());

        if (comments.isEmpty())
            comments.add("There is no comment about this defect."); // Default comment when there are no comments

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

        this.height = 260; // Height requirement to fit all static content.
        for (CommentModule comment : comments)
        {

            comment.establishHeight();
            this.height += comment.getHeight();

        }

    }

    @Override
    public void establishWidth() {

    }

    @Override
    public View initAndGetViews(Context context)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.module_defect, null, false);

        TextView title = v.findViewById(R.id.module_defect_title);
        StringBuilder defectTitle = new StringBuilder();
        defectTitle.append(defect.getName());
        if (defect.getGroup() != null)
            defectTitle.append(" - " + defect.getGroup().getName());
        else
            defectTitle.append(" - General");
        title.setText(defectTitle.toString());

        TextView priorityText = v.findViewById(R.id.module_defect_priority);
            priorityText.setText(defect.getSeverity().getPDFSeverity());
            priorityText.setTextColor(defect.getSeverity().getPDFColor(context));

        TextView imagesText = v.findViewById(R.id.module_defect_imageText);
        imagesText.setVisibility(hasImages ? View.VISIBLE : View.INVISIBLE);

        LinearLayout commentsContainer = v.findViewById(R.id.module_defect_comments);

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
