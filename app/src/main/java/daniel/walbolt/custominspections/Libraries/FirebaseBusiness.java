package daniel.walbolt.custominspections.Libraries;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import daniel.walbolt.custominspections.Inspector.Dialogs.Informative.ProgressDialog;
import daniel.walbolt.custominspections.Inspector.Home;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.Inspector.Pages.Main;

public class FirebaseBusiness
{

    private static FirebaseBusiness instance;

    private Home mHomePage;

    private FirebaseFirestore db;
    private FirebaseStorage imageDB;

    private int queries = 0;

    public FirebaseBusiness()
    {

        db = FirebaseFirestore.getInstance();
        imageDB = FirebaseStorage.getInstance();

    }

    public static FirebaseBusiness getInstance()
    {

        if(instance == null)
            instance = new FirebaseBusiness();

        return instance;

    }

    public void setHomePage(Home homePage)
    {

        mHomePage = homePage;

    }

    public void saveSchedule(Schedule schedule, Activity context)
    {

        //Turn the Schedule object into a HashMap list of key value pairs.
        Map<String, Object> scheduleMap = new HashMap<>();
        scheduleMap.put("client_first", schedule.client_first);
        scheduleMap.put("client_last", schedule.client_last);
        scheduleMap.put("client_email", schedule.email);
        scheduleMap.put("client_phone", schedule.phone);
        scheduleMap.put("address", schedule.address);
        scheduleMap.put("cost", schedule.cost);
        scheduleMap.put("duration", schedule.duration);
        scheduleMap.put("start", schedule.formatDate());
        scheduleMap.put("age", schedule.age);
        scheduleMap.put("utilities", schedule.utilities);
        scheduleMap.put("vacant", schedule.vacancy);
        scheduleMap.put("occupied", schedule.occupancy);
        scheduleMap.put("outbuilding", schedule.outbuilding);
        scheduleMap.put("footage", schedule.footage);

        //Reference the database object, and store the schedule in the appropriate place.
        //Schedules are to be stored in the "Schedules" folder, using the schedule's generated ID (generated using year,month,day,hour).
        db.collection("schedules")
                .document(schedule.getScheduleID()) //Get the document reference in the "schedules" folder
                .set(scheduleMap)// Set the data in the document to the data of the HashMap
                .addOnSuccessListener(new OnSuccessListener<Void>() { // Create a listener for when the document is uploaded
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Schedule uploaded", Toast.LENGTH_SHORT).show();;
                    }
                })
                .addOnFailureListener(new OnFailureListener() { // Create a listener for if the document fails to upload
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Schedule failed to upload", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public ArrayList<Schedule> loadSchedules()
    {

        final ArrayList<Schedule> savedSchedules = new ArrayList<>();

        db.collection("schedules").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            Date date = new Date();

                            try {
                                date = new SimpleDateFormat("yyyyMMdd@HHmm", Locale.US).parse((String) document.get("start"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            Schedule schedule = new Schedule("", calendar);
                            schedule.address = (String) document.get("address");
                            schedule.client_first = (String) document.get("client_first");
                            schedule.client_last = (String) document.get("client_last");
                            schedule.phone = (String) document.get("client_phone");
                            schedule.email = (String) document.get("client_email");
                            schedule.cost = (long) document.get("cost");
                            schedule.duration = (double) document.get("duration");
                            schedule.footage = (long) document.get("footage");
                            schedule.occupancy = (boolean) document.get("occupied");
                            schedule.outbuilding = (boolean) document.get("outbuilding");
                            schedule.utilities = (boolean) document.get("utilities");
                            schedule.vacancy = (boolean) document.get("vacant");
                            savedSchedules.add(schedule);
                            mHomePage.updateScheduledRecycler();

                        }

                    }

                });

        return savedSchedules;

    }

    public void removeSchedule(Schedule schedule, final Context context)
    {

        db.collection("schedules").document(schedule.getScheduleID())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Removed Schedule", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Removal Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /*public void startUpload(final ProgressDialog dialog, ArrayList<InspectionData> dataPoints)
    {

        int toProgress = 1;
        for(InspectionData data : dataPoints)
        {

            toProgress += data.getDataPoints();

        }
        final double totalToProgress = toProgress;

        System.out.println("Points to upload : " + totalToProgress);
        System.out.println("Every Point progress : " + 100*(1/totalToProgress));

        *//*

        First, save the client information

         *//*
        Map<String, Object> toSaveToDatabase = new HashMap<>();
        Map<String, Object> nestedMap = new HashMap<>();
        Schedule schedule = Main.inspectionSchedule;
        toSaveToDatabase.put("Address", schedule.address);
        nestedMap.put("Client First", schedule.client_first);
        nestedMap.put("Client Last", schedule.client_last);
        toSaveToDatabase.put("Client Information", nestedMap);
        toSaveToDatabase.put("Inspected Systems", schedule.inspection.getInspectedSystems());

        Map<String, Object> timeStamp = new HashMap<>();
        timeStamp.put("StartDate", schedule.getYear() + "/" + schedule.getMonth() + "/" + schedule.getDay());
        timeStamp.put("StartTime", schedule.getHour() + ":" + schedule.getMinutes());

        toSaveToDatabase.put("Timestamp", timeStamp);

        getInspectionDocument(schedule).set(toSaveToDatabase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.incrementProgress((int) (100*(1/totalToProgress)));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.setStatusText(e.getMessage());
                    }
                });

        *//*

        Next, loop through the data points array list.
        Each Map<String, Object> is the save() function output from each system accumulating it and its subsystems stored data.

         *//*

        *//*

        The Data Point for the Roof System could be established like this:

        InspectionData.getSystem() == SystemType.ROOF
        InspectionData.getSystemData() returns Map<String, Object>
        if(InspectionData.hasSubSystems()) (returns true in this case)
        {

            for(InspectionData subsystem : InspectionData.getSubSystems()
            {

                //Do the same and save these files underneath the subsystems collection.

            }

        }
         *//*

        for(final InspectionData system : dataPoints)
        {

            DocumentReference systemDocument = getInspectionDocument(InspectionMainPage.inspectionSchedule).collection("Systems").document(system.getSystemName());

            if(system.hasSubSystems())
            {

                for(final InspectionData subSystem : system.getSubSystemData())
                {

                    getInspectionDocument(Main.inspectionSchedule).collection("Systems").document(system.getSystemName()).collection("SubSystems").document(subSystem.getSystemName()).set(subSystem.getSystemData())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.incrementProgressBar((int) (100*(subSystem.getDataPoints()/totalToProgress)));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.failedToUpload(e.getMessage());
                                }
                            });

                    if(subSystem.hasPictures())
                        uploadImage(subSystem.getPictures());

                }

                system.addSubSystemList();

            }

            //Save the system data
            systemDocument.set(system.getSystemData())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.incrementProgress((int) (100*(system.getDataPoints()/totalToProgress)));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.setStatusText(e.getMessage());
                        }
                    });

        }

    }*/

    public void uploadPDF(Uri pdfURI, String name)
    {

        StorageReference storageReference = imageDB.getReference();
        StorageReference folderReference = storageReference.child("PDFs" + File.separator + name);
        folderReference.putFile(pdfURI);

    }

    public void uploadImage(Context context, ArrayList<InspectionMedia> media)
    {

        for(InspectionMedia aMedia : media)
        {

            Schedule inspectionSchedule = Main.inspectionSchedule;

            StorageReference storageReference = imageDB.getReference();
            StorageReference folderReference = storageReference.child(inspectionSchedule.getYear() + File.separator + inspectionSchedule.getMonth() + File.separator + inspectionSchedule.getDay() + File.separator + inspectionSchedule.getHour() + File.separator + aMedia.getFileName());
            folderReference.putFile(aMedia.getURI(context));

        }

    }

    //Retrieve an image from the database
    public void retrieveImage(Context context, InspectionMedia targetMedia)
    {

        Schedule schedule = Main.inspectionSchedule;
        StorageReference storageReference = imageDB.getReference();
        StorageReference folderReference = storageReference.child(schedule.getYear() + File.separator + schedule.getMonth() + File.separator + schedule.getDay() + File.separator + schedule.getHour() + File.separator + targetMedia.getFileName());
        folderReference.getFile(targetMedia.getURI(context));

    }

    //Retrieve the saved system data from the database
    public void fetchInspectionSystems(final Context context, final ProgressDialog dialog, final Main systemPage)
    {

        //TODO: Fix this loading mechanism so that every saved system is loaded, instead of looking for the systems we expect to find. (create a saved system list?)
        final Schedule schedule = Main.inspectionSchedule;

        final Map<String, Object> allSystemData = new HashMap<>();

        queries = 0;
        for(final String system : schedule.inspectedSystems)
        {

            queries += 2;
            final Map<String, Object> systemData = new HashMap<>();
            final Map<String, Object> subSystemList = new HashMap<>();

            dialog.setStatusText("Loading..." + system);
            getInspectionDocument(schedule).collection("Systems").document(system).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {

                                systemData.put("System", task.getResult().getData());
                                allSystemData.put(system, systemData);
                                dialog.setStatusText("Loaded: " + system);

                            }

                            queries--;

                            if(queries == 0) // If this is the last query
                            {

                                loadCheck(context, dialog, allSystemData, systemPage);

                            }

                        }
                    });
            getInspectionDocument(schedule).collection("Systems").document(system).collection("SubSystems").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {

                                for(QueryDocumentSnapshot subSystem : task.getResult())
                                {

                                    // <SUBSYSTEM_CONSTANT, DATA>
                                    subSystemList.put(subSystem.getId(), subSystem.getData());
                                    dialog.setStatusText("Loading..." + subSystem.getId());

                                }

                                systemData.put("SubSystems", subSystemList);

                            }

                            queries--;

                            if(queries == 0) // If this is the last query
                            {

                                loadCheck(context, dialog, allSystemData, systemPage);

                            }

                        }
                    });

        }

    }

    private void loadCheck(Context context, ProgressDialog dialog, Map<String, Object> allSystemData, Main systemPage)
    {

        dialog.dismiss();
        Main.inspectionSchedule.inspection.finalizeLoadedInspection(context, allSystemData, systemPage);

    }

    public void loadPastInspections(final Calendar date, boolean searchEntireMonth, final ArrayList<Schedule> pastInspections)
    {

        final String month = (date.get(Calendar.MONTH)+1) < 10 ? "0" + (date.get(Calendar.MONTH)+1) : String.valueOf((date.get(Calendar.MONTH)+1));

        final ArrayList<Schedule> savedInspections = new ArrayList<>();

        if(searchEntireMonth)
            db.collection("inspections").document(String.valueOf(date.get(Calendar.YEAR))).collection("Months").document(month).collection("Days").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {

                                for(final QueryDocumentSnapshot day : task.getResult())
                                {

                                    db.collection("inspections").document(String.valueOf(date.get(Calendar.YEAR))).collection("Months").document(month).collection("Days").document(day.getId()).collection("Hour").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {

                                                        for(DocumentSnapshot inspection : task.getResult())
                                                        {

                                                            // Recreate the schedule by gathering information from the inspection document.
                                                            //Recreate the start-date

                                                            Calendar thisDate = Calendar.getInstance();
                                                            thisDate.setTime(date.getTime());

                                                            thisDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.getId())); // Set the day
                                                            thisDate.set(Calendar.HOUR, Integer.parseInt(inspection.getId())); // Set the hour
                                                            Map<String, Object> timeStamp = (HashMap<String, Object>) inspection.get("Timestamp");
                                                            String militaryTime = (String) timeStamp.get("StartTime");
                                                            thisDate.set(Calendar.MINUTE, Integer.parseInt(militaryTime.replace(inspection.getId() + ":", ""))); // Set the minute start time

                                                            Schedule pastInspection = new Schedule((String) inspection.get("Address"), thisDate);
                                                            pastInspection.isPastInspection = true;

                                                            pastInspection.inspectedSystems = (ArrayList<String>) inspection.get("Inspected Systems");

                                                            //Recreate the client information
                                                            Map<String, Object> clientInfo = (HashMap<String, Object>) inspection.get("Client Information");
                                                            pastInspection.client_first = (String) clientInfo.get("Client First");
                                                            pastInspection.client_last = (String) clientInfo.get("Client Last");

                                                            savedInspections.add(pastInspection);

                                                        }


                                                    }

                                                    pastInspections.clear();
                                                    pastInspections.addAll(savedInspections);
                                                    mHomePage.updatePastInspectionRecycler();

                                                }
                                            });
                                }
                            }
                        }
                    });
    }

    private DocumentReference getInspectionDocument(Schedule schedule)
    {

        String weekDay = schedule.date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        Map<String, Object> dayInfo = new HashMap<>();
        dayInfo.put("Name", weekDay);

        db.collection("inspections").document(schedule.getYear()).collection("Months").document(schedule.getMonth()).collection("Days").document(schedule.getDay()).set(dayInfo);

        return db.collection("inspections").document(schedule.getYear()).collection("Months").document(schedule.getMonth()).collection("Days").document(schedule.getDay()).collection("Hour").document(schedule.getHour());

    }

}
