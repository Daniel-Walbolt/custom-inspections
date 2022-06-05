package daniel.walbolt.custominspections.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.Inspector.Objects.Other.Configuration;
import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.MainActivity;
import daniel.walbolt.custominspections.R;

public class InspectionActivity extends MainActivity
{

    private Schedule inspectionSchedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        inspectionSchedule = (Schedule) intent.getSerializableExtra("MySchedule");

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Main(this, inspectionSchedule);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!inspectionSchedule.isPastInspection)
            Configuration.saveInspectionConfiguration(this);
        inspectionSchedule = null;
    }

    @Override
    public void onBackPressed()
    {
        new ConfirmAlert(this, "Any data not uploaded will be deleted!", this);
    }

    public Schedule getSchedule()
    {

        return inspectionSchedule;

    }

}
