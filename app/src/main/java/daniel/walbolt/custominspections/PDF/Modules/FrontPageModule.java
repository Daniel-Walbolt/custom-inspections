package daniel.walbolt.custominspections.PDF.Modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.Inspector.Pages.Front;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.PDF.Objects.Module;
import daniel.walbolt.custominspections.R;

public class FrontPageModule extends Module
{

    public int totalDefects = 0;
    public int hpDefects = 0;
    public int restrictionCount = 0;
    public ArrayList<String> hpDefectSystems;
    public ArrayList<String> restrictionSystems;
    public ArrayList<String> excludedSystems;
    private Front frontSystem;

    public FrontPageModule(Front frontSystem)
    {

        //Front Dialog gathers the basic information

        hpDefectSystems = new ArrayList<>();
        restrictionSystems = new ArrayList<>();
        excludedSystems = new ArrayList<>();

        this.frontSystem = frontSystem;

    }

    @Override
    public void establishHeight() {
        this.height = PAGE_HEIGHT;
    }

    @Override
    public void establishWidth() {
        this.width = PAGE_WIDTH;
    }

    @Override
    public View initAndGetViews(Context context)
    {

        View view =  LayoutInflater.from(context).inflate(R.layout.pdf_front_page, null, false);

        Schedule schedule = Main.inspectionSchedule;

        ImageView frontImage = view.findViewById(R.id.pdf_front_image);
            frontImage.setImageURI(frontSystem.getFrontImage().getURI(context));

        //Get all the text views that display data
        TextView address = view.findViewById(R.id.pdf_front_address);
            address.setText(schedule.address);

        TextView temp = view.findViewById(R.id.pdf_front_temp);
            temp.setText(frontSystem.getTemp());

        TextView weather = view.findViewById(R.id.pdf_front_weather);
            weather.setText(frontSystem.getWeather());

        TextView time = view.findViewById(R.id.pdf_front_time);
            time.setText(schedule.getMonth() + "/" + schedule.getDay() + "/" + schedule.getYear() + " @ " + schedule.getHour() + ":" + schedule.getMinutes());

        TextView houseDetails = view.findViewById(R.id.pdf_front_house_details);
            houseDetails.setText(schedule.footage + " sq.ft. ~ " + schedule.age + " years old");

        /*
        TODO: Inspector details
         */

        TextView totalDefects = view.findViewById(R.id.pdf_front_total_defect_count);
            totalDefects.setText(this.totalDefects + " Total Defects");

        TextView hpDefects = view.findViewById(R.id.pdf_front_hp_defect_count);
            hpDefects.setText(this.hpDefects + " High Priority Defects");

        TextView hpDefectSystems = view.findViewById(R.id.pdf_front_hp_defect_systems);
            StringBuilder systems = new StringBuilder("Applicable Systems: " + (this.hpDefectSystems.size() == 0 ? "None" : ""));
            for(int i = 0; i <  this.hpDefectSystems.size(); i++)
            {

                String system = this.hpDefectSystems.get(i);

                systems.append(i > 0 ? ", " + system : system);

            }

            hpDefectSystems.setText(systems.toString());

        TextView restrictionCount = view.findViewById(R.id.pdf_front_restrictions_count);
            restrictionCount.setText(this.restrictionCount + " Restrictions");

        TextView restrictionSystems = view.findViewById(R.id.pdf_front_restrictions_systems);
            StringBuilder restrictedSystems = new StringBuilder("Applicable Systems: " + (this.restrictionSystems.size() == 0 ? "None" : ""));
            for (int i = 0; i < this.restrictionSystems.size(); i++)
            {

                String restrictedSystem = this.restrictionSystems.get(i);

                restrictedSystems.append(i > 0 ? ", " + restrictedSystem : restrictedSystem);

            }
            restrictionSystems.setText(restrictedSystems.toString());

        TextView excludedSystems= view.findViewById(R.id.pdf_front_excluded_systems);
            StringBuilder exclusions = new StringBuilder("Excluded Systems: ");
            for (int i = 0 ; i < this.excludedSystems.size(); i++ )
            {

                String excludedSystem = this.excludedSystems.get(i);

                exclusions.append(i > 0 ? ", " + excludedSystem : excludedSystem);

            }
            excludedSystems.setText(exclusions.toString());

        return view;

    }
}
