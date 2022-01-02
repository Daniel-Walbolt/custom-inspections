package daniel.walbolt.custominspections.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CalendarView;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;

import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.Inspector.Pages.Scheduler;
import daniel.walbolt.custominspections.MainActivity;
import daniel.walbolt.custominspections.R;

public class ScheduleActivity extends MainActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        new Scheduler(this);

    }

    @Override
    public void onBackPressed() {
        new ConfirmAlert(this, "The schedule information will not be saved.", this);
    }

}
