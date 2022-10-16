package daniel.walbolt.custominspections.Inspector.Objects;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import daniel.walbolt.custominspections.Libraries.FirebaseBusiness;

public class Schedule implements Serializable
{

    /*

    Schedules are an object used by both the database and inspection class to display information about:
    General Residence Info
    Client Contact info
    Service Type and Times


     */

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
    public String entryMethod;
    public String entryNotes;
    public int age;

    /*
    Inspection Info
     */
    public long cost;
    public ArrayList<String> inspectedSystems;

    public Inspection inspection;

    public Schedule(String address, Calendar date)
    {

        this.address = address;
        this.date = date;

    }

    public void upload(Activity activity)
    {

        new FirebaseBusiness().saveSchedule(this, activity);

    }

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

    public Map<String, Object> saveSchedule()
    {

        //Turn the Schedule object into a HashMap list of key value pairs.
        Map<String, Object> scheduleMap = new HashMap<>();
        scheduleMap.put("client_first", client_first);
        scheduleMap.put("client_last", client_last);
        scheduleMap.put("client_email", email);
        scheduleMap.put("client_phone", phone);
        scheduleMap.put("address", address);
        scheduleMap.put("cost", cost);
        scheduleMap.put("duration", duration);
        scheduleMap.put("start", formatDate());
        scheduleMap.put("age", age);
        scheduleMap.put("utilities", utilities);
        scheduleMap.put("vacant", vacancy);
        scheduleMap.put("detached_garage", detachedGarage);
        scheduleMap.put("entryMethod", entryMethod);
        scheduleMap.put("entryNotes", entryNotes);
        scheduleMap.put("occupied", occupancy);
        scheduleMap.put("outbuilding", outbuilding);
        scheduleMap.put("footage",footage);

        return scheduleMap;

    }

    public void loadFrom(DocumentSnapshot document)
    {

        address = (String) document.get("address");
        client_first = (String) document.get("client_first");
        client_last = (String) document.get("client_last");
        phone = (String) document.get("client_phone");
        email = (String) document.get("client_email");
        cost = (long) document.get("cost");
        duration = (double) document.get("duration");
        footage = (long) document.get("footage");
        occupancy = (boolean) document.get("occupied");
        outbuilding = (boolean) document.get("outbuilding");
        utilities = (boolean) document.get("utilities");
        vacancy = (boolean) document.get("vacant");
        if(document.contains("detached_garage"))
            detachedGarage = (boolean) document.get("detached_garage");
        entryMethod = (String) document.get("entryMethod");
        entryNotes = (String) document.get("entryNotes");

    }

}
