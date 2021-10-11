package daniel.walbolt.custominspections.Inspector.Objects;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Schedule
{

    public String address;
    public Calendar date;
    public boolean isSigned = false;
    public boolean isPastInspection = false;

    /*
    Client Info
     */
    public String client_first;
    public String client_last;
    public String email;
    public String phone;

    /*
    Time
     */
    public double duration;

    /*
    Property Info
     */
    public long footage;
    public boolean vacancy;
    public boolean utilities;
    public boolean outbuilding;
    public boolean occupancy;
    public boolean detachedGarage;
    public int age;

    /*
    Inspection Info
     */
    public long cost;
    public ArrayList<String> inspectedSystems;

    public Schedule(String address, Calendar date)
    {

        this.address = address;
        this.date = date;

    }

//    public void save()
//    {
//
//        FirebaseBusiness.getInstance().saveSchedule(this);
//
//    }

//    public void unschedule(Context context)
//    {
//
//        FirebaseBusiness.getInstance().removeSchedule(this, context);
//
//    }

    public String formatDate()
    {

        return new SimpleDateFormat("yyyyMMdd@HHmm", Locale.US).format(date.getTime());

    }

    public boolean isSoonerThan(Schedule schedule)
    {

        return this.date.before(schedule.date);

    }

    public String getYear() // This method is used for the database save-path
    {

        return String.valueOf(date.get(Calendar.YEAR));

    }

    public String getMonth()
    {

        String month = "";

        month = String.valueOf(date.get(Calendar.MONTH) + 1);

        if((date.get(Calendar.MONTH) + 1) < 10)
        {

            month = "0" + month;

        }

        return month;

    }

    public String getDay()
    {

        String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        if(date.get(Calendar.DAY_OF_MONTH) < 10)
        {

            day = "0" + day;

        }

        return day;

    }

    public String getHour()
    {

        String hour = String.valueOf(date.get(Calendar.HOUR_OF_DAY));
        if(date.get(Calendar.HOUR_OF_DAY) < 10)
        {

            hour = "0" + hour;

        }

        return hour;

    }

    public String getMinutes()
    {

        String minute = String.valueOf(date.get(Calendar.MINUTE));
        if(date.get(Calendar.MINUTE) < 10)
        {

            minute = "0" + minute;

        }

        return minute;

    }

    public String getScheduleID()
    {

        StringBuilder id = new StringBuilder();
        id.append(date.get(Calendar.YEAR));
        id.append(getMonth());
        id.append(getDay());
        id.append(getHour());

        return id.toString();

    }

}
