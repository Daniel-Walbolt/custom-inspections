package daniel.walbolt.custominspections.Inspector.Dialogs.Informative;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.R;

public class ClientDialog extends Dialog
{

    public ClientDialog(Activity mActivity, Schedule schedule)
    {

        super(mActivity);

        setContentView(R.layout.client);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);

        initValues(schedule);

        show();

    }

    private void initValues(Schedule schedule)
    {

        TextView address = findViewById(R.id.inspection_schedule_address);
        address.setText(schedule.address);

        TextView date = findViewById(R.id.inspection_schedule_date);
        String dateText = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(schedule.date.getTime());
        date.setText(dateText);

        TextView start = findViewById(R.id.inspection_schedule_start);
        int startHour = schedule.date.get(Calendar.HOUR_OF_DAY) > 12 && schedule.date.get(Calendar.HOUR_OF_DAY) != 24 ? schedule.date.get(Calendar.HOUR_OF_DAY) - 12 : schedule.date.get(Calendar.HOUR_OF_DAY);
        String startMinute = schedule.date.get(Calendar.MINUTE) < 10 ? "0" + schedule.date.get(Calendar.MINUTE) : String.valueOf(schedule.date.get(Calendar.MINUTE));
        String timePeriod = schedule.date.get(Calendar.HOUR_OF_DAY) > 12 && schedule.date.get(Calendar.HOUR_OF_DAY) != 24 ? " PM" : " AM";
        start.setText(startHour + ":" + startMinute + timePeriod);

        TextView duration = findViewById(R.id.inspection_schedule_duration);
        String[] totalDuration = String.valueOf(schedule.duration).split("\\.");
        int durationHours = Integer.parseInt(totalDuration[0]);
        int durationMinutes = (int) (Double.parseDouble(totalDuration[1]) / 100 * 60);
        String durationTime = durationHours + "h" + durationMinutes + "m";
        duration.setText(durationTime);

        TextView cost = findViewById(R.id.inspection_schedule_cost);
        cost.setText("$" + schedule.cost);

        TextView age = findViewById(R.id.inspection_schedule_age);
        String ageText = schedule.age + " years (" + (schedule.date.get(Calendar.YEAR) - schedule.age) + ")";
        age.setText(ageText);

        TextView footage = findViewById(R.id.inspection_schedule_footage);
        footage.setText(schedule.footage + "sqFt");
        TextView vacant = findViewById(R.id.inspection_schedule_vacancy);
        vacant.setText(booleanToString(schedule.vacancy));
        TextView occupied = findViewById(R.id.inspection_schedule_occupied);
        occupied.setText(booleanToString(schedule.occupancy));
        TextView utilities = findViewById(R.id.inspection_schedule_utilities);
        utilities.setText(booleanToString(schedule.utilities));
        TextView outbuilding = findViewById(R.id.inspection_schedule_outbuilding);
        outbuilding.setText(booleanToString(schedule.outbuilding));
        TextView detachedGarage = findViewById(R.id.inspection_schedule_detached);
        detachedGarage.setText(booleanToString(schedule.detachedGarage));

        TextView entryMethod = findViewById(R.id.inspection_schedule_entry_method);
        entryMethod.setText(schedule.entryMethod);

        TextView entryNotes = findViewById(R.id.inspection_schedule_entry_notes);
        entryNotes.setText(schedule.entryNotes);

        TextView name = findViewById(R.id.inspection_schedule_client_name);
        name.setText(schedule.client_first + " " + schedule.client_last);

        TextView email = findViewById(R.id.inspection_schedule_client_email);
        email.setText(schedule.email);

        TextView phone = findViewById(R.id.inspection_schedule_client_phone);
        phone.setText(schedule.phone);

    }

    private String booleanToString(boolean bool)
    {

        if(bool)
            return "Yes";
        else
            return "No";

    }

}
