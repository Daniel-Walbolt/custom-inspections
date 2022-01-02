package daniel.walbolt.custominspections.Inspector.Pages;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

import daniel.walbolt.custominspections.Activities.ScheduleActivity;
import daniel.walbolt.custominspections.Inspector.Home;
import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.R;

public class Scheduler
{

    private ScheduleActivity mActivity;

    //user input fields
    private EditText first;
    private EditText last;
    private EditText footage;
    private EditText address;
    private EditText year;
    private EditText entryNotes;
    private EditText email;
    private EditText phone;
    private TimePicker time;
    private CalendarView calendar;

    private CheckBox vacant;
    private CheckBox utilities;
    private CheckBox outbuilding;
    private CheckBox occupied;
    private CheckBox detachedGarage;
    private RadioButton clientEntry;
    private RadioButton unlockedEntry;
    private RadioButton keyEntry;

    // User input RESULT views
    private TextView price;
    private TextView duration;


    //Scrollview
    private ScrollView scroller;

    //Intermediate variables
    private int hour;
    private int minutes;
    private Calendar date;

    private double hourNumber;

    //Set the activity's page as a new schedule layout
    public Scheduler(ScheduleActivity activity)
    {

        this.mActivity = activity;
        activity.setContentView(R.layout.scheduler);

        //Initialize the default values
        initButtons();

    }

    private void initButtons()
    {


        //Find and set listeners on edit text fields. Whenever the text field is made empty AGAIN, the hint will be restored.
        first = mActivity.findViewById(R.id.inspection_scheduler_client_first);
        first.setOnFocusChangeListener(getFieldFocusChangeListener("First Name"));
        last = mActivity.findViewById(R.id.inspection_scheduler_client_last);
        last.setOnFocusChangeListener(getFieldFocusChangeListener("Last Name"));
        footage = mActivity.findViewById(R.id.inspection_scheduler_footage);
        footage.setOnFocusChangeListener(getFieldFocusChangeListener("Square Feet"));
        year = mActivity.findViewById(R.id.inspection_scheduler_yearbuilt);
        year.setOnFocusChangeListener(getFieldFocusChangeListener("Year Built"));
        entryNotes = mActivity.findViewById(R.id.inspection_scheduler_entry_method);
        entryNotes.setOnFocusChangeListener(getFieldFocusChangeListener("Other / Notes"));
        address = mActivity.findViewById(R.id.inspection_scheduler_address);
        address.setOnFocusChangeListener(getFieldFocusChangeListener("Address"));
        email = mActivity.findViewById(R.id.inspection_scheduler_client_email);
        email.setOnFocusChangeListener(getFieldFocusChangeListener("Email"));
        phone = mActivity.findViewById(R.id.inspection_scheduler_client_phone);
        phone.setOnFocusChangeListener(getFieldFocusChangeListener("Phone"));

        //Initialize the scroll view
        scroller = mActivity.findViewById(R.id.scheduler_scroll_view);

        //Set the time of inspection to 7 am by default. Update the hour and minute variable whenever it is changed.
        time = mActivity.findViewById(R.id.inspection_scheduler_time);
        time.setHour(hour);
        time.setMinute(minutes);
        time.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            hour = hourOfDay;
            minutes = minute;
        });

        //Find the calendar objects and set the value to Today's date. Whenever it is changed, update the saved year, month, and day.
        if(date == null)
            date = Calendar.getInstance();

        calendar = mActivity.findViewById(R.id.inspection_scheduler_date);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date.set(Calendar.YEAR, year);
                date.set(Calendar.MONTH, month);
                date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        });

        //Find and initialize the functionality of the OPTIONAL checkboxes.
        vacant = mActivity.findViewById(R.id.inspection_scheduler_vacant);
        vacant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculatePrice();
            }
        });
        utilities = mActivity.findViewById(R.id.inspection_scheduler_utilities);
        utilities.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculatePrice();
            }
        });
        outbuilding = mActivity.findViewById(R.id.inspection_scheduler_outbuilding);
        outbuilding.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculatePrice();
            }
        });
        occupied = mActivity.findViewById(R.id.inspection_scheduler_occupied);
        occupied.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculatePrice();
            }
        });
        detachedGarage = mActivity.findViewById(R.id.inspection_scheduler_detached);
        detachedGarage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculatePrice();
            }
        });

        clientEntry = mActivity.findViewById(R.id.inspection_scheduler_entry_client);
        unlockedEntry = mActivity.findViewById(R.id.inspection_scheduler_entry_unlocked);
        keyEntry = mActivity.findViewById(R.id.inspection_scheduler_entry_hidden);

        price = mActivity.findViewById(R.id.inspection_scheduler_price);
        duration = mActivity.findViewById(R.id.inspection_scheduler_duration);

        Button finish = mActivity.findViewById(R.id.inspection_scheduler_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkScheduleErrors();
            }
        });

    }

    private void checkScheduleErrors()
    {

        if(!first.getText().toString().replaceAll(" ", "").isEmpty())
        {

            if(!last.getText().toString().replaceAll(" ", "").isEmpty())
            {

                if(!email.getText().toString().replaceAll(" ", "").isEmpty() || !phone.getText().toString().isEmpty())
                {

                    if(!address.getText().toString().replaceAll(" ", "").isEmpty())
                    {

                        if(!year.getText().toString().isEmpty())
                        {

                            if(!footage.getText().toString().isEmpty())
                            {

                                saveSchedule();

                            }
                            else
                                autoScrollTo(footage);

                        }
                        else
                            autoScrollTo(year);

                    }
                    else
                        autoScrollTo(address);

                }
                else
                    autoScrollTo(email);

            }
            else
                autoScrollTo(last);

        }
        else
            autoScrollTo(first);

    }

    private Calendar getScheduledTime()
    {

        Calendar date = Calendar.getInstance();
        date.setTime(this.date.getTime());
        date.set(Calendar.HOUR_OF_DAY, time.getHour());
        date.set(Calendar.MINUTE, time.getMinute());

        return date;

    }

    private void saveSchedule()
    {

        //Create a schedule object which can be used to upload to database
        Schedule schedule = new Schedule(address.getText().toString(), getScheduledTime());
        schedule.client_first = first.getText().toString();
        schedule.client_last = last.getText().toString();
        schedule.phone = phone.getText().toString();
        schedule.age = Integer.parseInt(year.getText().toString());
        schedule.email = email.getText().toString();
        schedule.cost = Integer.parseInt(price.getText().toString());
        schedule.duration = hourNumber;
        schedule.footage = Integer.parseInt(footage.getText().toString());
        schedule.vacancy = vacant.isChecked();
        schedule.utilities = utilities.isChecked();
        schedule.outbuilding = outbuilding.isChecked();
        schedule.occupancy = occupied.isChecked();
        schedule.entryNotes = entryNotes.getText().toString();
        schedule.entryMethod = getEntryMethod();
        schedule.upload(mActivity); // Upload the schedule
        mActivity.finish(); // Close the ScheduleActivity, which reopens the home screen

    }

    private String getEntryMethod()
    {

        String entryMethod = "None specified";

        if(clientEntry.isChecked())
            entryMethod = clientEntry.getText().toString();
        else if(keyEntry.isChecked())
            entryMethod = keyEntry.getText().toString();
        else if(unlockedEntry.isChecked())
            entryMethod = unlockedEntry.getText().toString();

        return entryMethod;

    }

    private void calculatePrice()
    {

        int price = mActivity.getResources().getInteger(R.integer.standard_price);
        int duration = mActivity.getResources().getInteger(R.integer.standard_time);

        if(!year.getText().toString().isEmpty()) // Calculate the extra price and time based on age of the house
        {

            int age = Integer.parseInt(year.getText().toString());
            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            age = Math.abs(today.get(Calendar.YEAR) - age);
            int age_limit = mActivity.getResources().getInteger(R.integer.age_limit);
            int cost_increment = mActivity.getResources().getInteger(R.integer.age_cost_increment);
            int time_increment = mActivity.getResources().getInteger(R.integer.age_time_increment);

            price += age >= age_limit ? cost_increment : 0;
            duration += age >= age_limit ? time_increment : 0;

        }

        if(!footage.getText().toString().isEmpty()) // Calculate the extra price and time based on square footage
        {

            int sqrFeet = Integer.parseInt(footage.getText().toString());
            int footage_limit = mActivity.getResources().getInteger(R.integer.footage_limit);
            int increment = mActivity.getResources().getInteger(R.integer.footage_increment);
            int priceIncrement = mActivity.getResources().getInteger(R.integer.footage_cost_increment);
            int timeIncrement = mActivity.getResources().getInteger(R.integer.footage_time_increment);

            if(sqrFeet >= footage_limit)
            {

                price += (priceIncrement + (priceIncrement * (int)((sqrFeet - footage_limit)/increment)));
                duration += (timeIncrement + (timeIncrement * (int)((sqrFeet-footage_limit)/increment)));

            }

        }

        if(outbuilding.isChecked()) {
            price += mActivity.getResources().getInteger(R.integer.outbuilding_cost);
            duration += mActivity.getResources().getInteger(R.integer.outbuilding_time);
        }

        if(detachedGarage.isChecked()) {
            price += mActivity.getResources().getInteger(R.integer.detached_garage_cost);
            duration += mActivity.getResources().getInteger(R.integer.detached_garage_time);
        }

        int hours = duration / 60;
        int minutes = duration - (hours*60);
        this.hourNumber = hours + ((double)minutes)/60;
        this.duration.setText(hours + "h" + minutes + "m");
        this.price.setText(String.valueOf(price));

    }

    private void autoScrollTo(View target)
    {

        scroller.post(new Runnable() {
            @Override
            public void run() {
                scroller.smoothScrollTo(0, target.getBottom() + 1500);
                target.requestFocus();

                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(target, InputMethodManager.SHOW_FORCED);

            }
        });

    }

    private View.OnFocusChangeListener getFieldFocusChangeListener(final CharSequence originalHint)
    {

        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    ((EditText)view).setHint("");
                else {
                    ((EditText) view).setHint(originalHint);
                    calculatePrice();
                }
            }
        };

    }

}
