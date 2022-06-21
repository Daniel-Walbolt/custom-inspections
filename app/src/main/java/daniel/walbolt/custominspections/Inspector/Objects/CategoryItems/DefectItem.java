package daniel.walbolt.custominspections.Inspector.Objects.CategoryItems;

import android.content.Context;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Objects.Categories.Category;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MajorComponent;
import daniel.walbolt.custominspections.Inspector.Objects.Other.MediaRecycler;

public class DefectItem extends CategoryItem
{

    //False means that this defect requires service/repair
    private SEVERITY severity = SEVERITY.MAINTAIN; // default severity level

    public DefectItem(String name, Category category)
    {

        super(name, category);

    }

    public DefectItem(String name, Category category, long ID) {
        super(name, category, ID);
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

    @Override
    public Map<String, Object> save(InspectionData saveTo)
    {

        Map<String, Object> defectData = super.save(saveTo);

        //Save the priority of this defect
        defectData.put("Severity", severity.toString());

        return defectData;
    }

    @Override
    public void loadFrom(Context context, Map<String, Object> itemData)
    {

        super.loadFrom(context, itemData);

        if(itemData.containsKey("Severity"))
            severity = SEVERITY.valueOf((String) itemData.get("Severity"));

    }


    public enum SEVERITY {
        MAINTAIN {
            @Override
            public String getPDFSeverity() {
                return "Monitor & Maintain";
            }

            @Override
            public int getProgress() {
                return 0;
            }
        },
        MILD {
            @Override
            public String getPDFSeverity() {
                return "Mild";
            }

            @Override
            public int getProgress() {
                return 1;
            }
        },
        HIGH {
            @Override
            public String getPDFSeverity() {
                return "High";
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
