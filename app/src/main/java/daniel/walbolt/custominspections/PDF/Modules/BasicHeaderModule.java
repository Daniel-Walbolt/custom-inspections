package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.View;

import daniel.walbolt.custominspections.PDF.Chapter;
import daniel.walbolt.custominspections.PDF.Module;

/*
The BasicHeader has a simple design that signifies the current category, system, and page #.
 */

public class BasicHeaderModule extends Module
{

    private Chapter chapter;

    public BasicHeaderModule(Chapter chapter)
    {

        this.chapter = chapter;

    }


    @Override
    public void establishHeight() {

    }

    @Override
    public void establishWidth() {
        width = PAGE_WIDTH;
    }

    @Override
    public View initAndGetViews(Context context) {
        return null;
    }
}
