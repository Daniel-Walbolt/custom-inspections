package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MediaRecycler;

public class DefectItem extends CategoryItem
{

    //False means that this defect requires service/repair
    private boolean isMonitorAndMaintain = true;
    private SEVERITY severity = SEVERITY.LOW;

    public DefectItem(String name, Category category)
    {

        super(name, category);

    }

    public void setMonitorAndMaintain(boolean isMonitorAndMaintain)
    {

        this.isMonitorAndMaintain = isMonitorAndMaintain;

    }

    public boolean isMonitorAndMaintain()
    {

        return isMonitorAndMaintain;

    }

    public void setSeverity(SEVERITY severity)
    {

        this.severity = severity;

    }

    public SEVERITY getSeverity()
    {

        return this.severity;

    }

    public void initMedia(RecyclerView mediaRecycler, TextView emptyView)
    {

        new MediaRecycler(this, mediaRecycler, emptyView);

    }

    public enum SEVERITY {
        MONITOR_MAINTAIN {
            @Override
            public String getPDFSeverity() {
                return "Monitor & Maintain";
            }

            @Override
            public int getProgress() {
                return 0;
            }
        },
        LOW {
            @Override
            public String getPDFSeverity() {
                return "Low Priority";
            }

            @Override
            public int getProgress() {
                return 0;
            }
        },
        MEDIUM {
            @Override
            public String getPDFSeverity() {
                return "Medium Priority";
            }

            @Override
            public int getProgress() {
                return 1;
            }
        },
        HIGH {
            @Override
            public String getPDFSeverity() {
                return "High Priority";
            }

            @Override
            public int getProgress() {
                return 2;
            }
        };

        public abstract String getPDFSeverity();
        public abstract int getProgress(); // Return the appropriate progress of the defect slider

    }

}
