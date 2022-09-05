package daniel.walbolt.custominspections.PDF.Modules;

import android.app.ActionBar;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import daniel.walbolt.custominspections.PDF.Objects.Module;

public class SpacerModule extends Module
{

    private final int SPACING;

    public SpacerModule(int pixelSpacing)
    {

        this.SPACING = pixelSpacing;

    }

    @Override
    public void establishHeight() {
        this.height = SPACING;
    }

    @Override
    public void establishWidth() {
        this.width = PAGE_WIDTH;
    }

    @Override
    public View initAndGetViews(Context context)
    {

        View spacer = new View(context);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, SPACING));

        return spacer;
    }

}
