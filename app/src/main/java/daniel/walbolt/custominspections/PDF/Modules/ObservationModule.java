package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Observations;
import daniel.walbolt.custominspections.Inspector.Objects.CategoryItems.ObservationItem;
import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.R;

public class ObservationModule extends Module
{

    /*
    The ObservationModule controls how an ObservationItem is displayed on the PDF.
     */

    private ObservationItem observation;
    private ArrayList<CommentModule> comments;
    private boolean hasImages = true; // Should this module display the "Images" textview?

    public ObservationModule(Context context, ObservationItem observation)
    {

        this.observation = observation;
        this.comments = new ArrayList<>();

        ArrayList<String> comments = new ArrayList<>(observation.getCommentMedia().getComments());

        if (comments.isEmpty())
            comments.add("There is nothing to comment on."); // Default comment when there are no comments

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

        this.height = 210;
        for (CommentModule comment : comments)
        {

            comment.establishHeight();
            this.height += comment.getHeight();

        }

    }

    @Override
    public void establishWidth()
    {
        this.width = 1200;
    }

    @Override
    public View initAndGetViews(Context context)
    {

        View v = LayoutInflater.from(context).inflate(R.layout.module_observation, null, false);

        //Set the title of the observation to include the group its apart of
        TextView title = v.findViewById(R.id.module_observation_title);
            StringBuilder observationTitle = new StringBuilder();
            observationTitle.append(observation.getName());
            if (observation.getGroup() != null)
                observationTitle.append(" - " + observation.getGroup().getName());
            else
                observationTitle.append(" - General");
            title.setText(observationTitle.toString());

        TextView imagesText = v.findViewById(R.id.module_observation_imageText);
            imagesText.setVisibility(hasImages ? View.VISIBLE : View.INVISIBLE);

        LinearLayout commentsContainer = v.findViewById(R.id.module_observation_comments);

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
