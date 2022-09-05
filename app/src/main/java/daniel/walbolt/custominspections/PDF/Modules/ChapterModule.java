package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import daniel.walbolt.custominspections.Constants.SystemTags;
import daniel.walbolt.custominspections.PDF.Objects.Chapter;
import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.PDF.Objects.TableMark;
import daniel.walbolt.custominspections.R;

public class ChapterModule extends Module
{

    /*
    The ChapterModule controls the display of chapter intro-pages.
    These pages feature the name of the system, its parent system if applicable,
    # of High Priority Defects, # of all defects, # of restrictions, and a table of the location of each High Priority Defect.
     */

    private Chapter chapter;

    public ChapterModule(Chapter chapter)
    {

        this.chapter = chapter;

    }

    @Override
    public void establishHeight()
    {
        height = PAGE_HEIGHT;
    }

    @Override
    public void establishWidth()
    {
        width = PAGE_WIDTH;
    }

    @Override
    public View initAndGetViews(Context context)
    {

        //Initialize this modules views onto the page layout
        View view = LayoutInflater.from(context).inflate(R.layout.module_chapter_start, null, false);

        TextView chapterTitle = view.findViewById(R.id.module_chapter_title);
            chapterTitle.setText(chapter.title);

        LinearLayout systemTags =  view.findViewById(R.id.module_chapter_tags);

        for (SystemTags tag : chapter.getTags())
        {

            View tagLayout = LayoutInflater.from(context).inflate(R.layout.module_system_tag, systemTags, false); // Create the tag layout

            Drawable tagColor = ContextCompat.getDrawable(context, R.drawable.system_tag_background); //Get the background layout for the tag
            tagColor.setTint(tag.getTagColor(context)); // Set the color of the background depending on the tag itself

            tagLayout.setBackground(tagColor);

            TextView tagTitle = tagLayout.findViewById(R.id.module_systemtag_title);
                tagTitle.setText(tag.getPDFString());

            systemTags.addView(tagLayout); // Attach the tag to the chapter page

        }

        TextView highPriorityDefectCount = view.findViewById(R.id.module_chapter_hp_defects);
        TextView allDefectCount = view.findViewById(R.id.module_chapter_all_defects);
        TextView restrictionCount = view.findViewById(R.id.module_chapter_restrictions);
        LinearLayout defectTable = view.findViewById(R.id.module_chapter_defect_table);

        //This is a small feature, but if there is only 1 defect, get rid of the plural form
        String grammarDefect =  chapter.highPriorityDefectCount == 1 ? "Defect" :"Defects";

        highPriorityDefectCount.setText(chapter.highPriorityDefectCount + " High Priority " + grammarDefect);

        grammarDefect = chapter.allDefectCount == 1 ? "Defect" : "Defects";

        allDefectCount.setText(chapter.allDefectCount + " Total " + grammarDefect);

        String grammarRestriction = chapter.restrictionCount == 1 ? "Restriction" : "Restrictions";

        restrictionCount.setText(chapter.restrictionCount + " " + grammarRestriction);

        //Iterate through all the TableMarks in the chapter, and render them!
        for(TableMark highPriorityDefect : chapter.marks)
        {

            View tableMark = highPriorityDefect.initAndGetViews(context);

            //Right now, there is no check if the table marks will go past the LinearLayout.
            defectTable.addView(tableMark);

        }

        return view;
    }

}
