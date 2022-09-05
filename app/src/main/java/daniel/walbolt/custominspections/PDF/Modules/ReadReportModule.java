package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.R;

public class ReadReportModule extends Module
{

    @Override
    public void establishHeight()
    {

        this.height = PAGE_HEIGHT;

    }

    @Override
    public void establishWidth() {
        this.width = PAGE_WIDTH;
    }

    @Override
    public View initAndGetViews(Context context)
    {
        return LayoutInflater.from(context).inflate(R.layout.pdf_how_to_read_report, null, false);
    }
}
