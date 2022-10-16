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
import daniel.walbolt.custominspections.Inspector.Pages.Home;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionData;
import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.Inspector.Objects.Schedule;
import daniel.walbolt.custominspections.Inspector.Pages.Main;

public class FirebaseBusiness
{

    //FireBase is the database this app communicates with.
    //The app stores inspection data to the database, including Strings, numbers, booleans, and images.
    //THe app can also load from the database. Filling out the data of the inspection with the data loaded from the inspection.

    private FirebaseFirestore db;
    private FirebaseStorage imageDB;

    public FirebaseBusiness()
    {

        db = FirebaseFirestore.getInstance();

        imageDB = FirebaseStorage.getInstance();

    }

    //Save an inspection schedule to the database
    public void saveSchedule(Schedule schedule, Activity context)
    {

        //Turn the Schedule object into a HashMap list of key value pairs.
        Map<String, Object> scheduleMap = schedule.saveSchedule();

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

    // Fetch the schedules from the database to be displayed on the home page.
    public ArrayList<Schedule> loadSchedules(Home homePage)
    {

        //Create a list to store the loaded schedules
        final ArrayList<Schedule> savedSchedules = new ArrayList<>();

        //Get the collection of schedules in the database
        db.collection("schedules").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //Loop through every schedule document in the collection
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            //Create a date to store the start date of the schedule
                            Date date = new Date();

                            try {
                                date = new SimpleDateFormat("yyyyMMdd@HHmm", Locale.US).parse((String) document.get("start"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            //Create a calendar object from the date time
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);

                            //Create a new schedule object with a dummy address and the time it was first inspected.
                            Schedule schedule = new Schedule("", calendar);

                            //Load the schedule by passing the database document
                            schedule.loadFrom(document);

                            //Add the schedule to the list of loaded schedules
                            savedSchedules.add(schedule);

                        }

                        //Update the homepage after all schedules have been created.
                        homePage.updateScheduledRecycler(savedSchedules);

                    }

                });

        return savedSchedules;

    }

    // Remove a specific schedule from the database
    public void removeSchedule(Schedule schedule, final Context context)
    {

        //Get the expected location of the  schedule.
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

    public void startUpload(final ProgressDialog dialog, ArrayList<InspectionData> dataPoints)
    {

        // Progress variable indicates the amount of HashMaps to upload.
        // We start at 1, because every inspection has its generic information.
        int toProgress = 1;
        for(InspectionData data : dataPoints)
        {

            //Get the amount of HashMaps that are going to be uploaded
            toProgress += data.getDataPoints();

        }

        //The increment is the amount that each HashMap contributes to 100% upload
        final double increment = Math.ceil(100 * (1.0 / toProgress));

        System.out.println("Points to upload : " + toProgress);
        System.out.println("Incrementing by : " + increment + " per HashMap");

        //First, we save the client information to the inspection document
        //Create the HashMap that stores the inspection information
        Map<String, Object> inspectionInformation = new HashMap<>();

        //Get the inspection information
        Schedule schedule = Main.inspectionSchedule;
        inspectionInformation.put("Address", schedule.address);
        inspectionInformation.put("Inspected Systems", schedule.inspection.getInspectedSystems());

        //Create a HashMap that stores the client information
        Map<String, Object> clientInformation = new HashMap<>();
        clientInformation.put("Client First", schedule.client_first);
        clientInformation.put("Client Last", schedule.client_last);
        inspectionInformation.put("Client Information", clientInformation);

        //Create a HashMap to store the date and time of inspection
        Map<String, Object> timeStamp = new HashMap<>();
        timeStamp.put("StartDate", schedule.getYear() + "/" + schedule.getMonth() + "/" + schedule.getDay());
        timeStamp.put("StartTime", schedule.getHour() + ":" + schedule.getMinutes());
        inspectionInformation.put("Timestamp", timeStamp);

        //Next, get the inspection document from the database, and set its information to the inspection information HashMap.
        getInspectionDocument(schedule).set(inspectionInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //When the upload is successful, increment the progress dialog
                        dialog.incrementProgress(increment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //If the upload fails, increment the progress dialog
                        dialog.setStatusText(e.getMessage());

                    }
                });



        /*

        Next, loop through the data points array list again.
        Each Map<String, Object> is the save() function output from each system accumulating its, and its subsystems, stored data.

        */
        for(InspectionData systemData : dataPoints)
        {

            //Get the system's document
            DocumentReference systemDocument = getSystemDocument(schedule, systemData.getSystemName());

            //Loop through all the SubSystem data and save each subsystem in its own document
            for(final InspectionData subSystemData : systemData.getSubSystemData())
            {

                //Get the sub system's document
                DocumentReference subSystemDocument = systemDocument.collection("Sub Systems").document(subSystemData.getSystemName());

                //Save the Sub System's categories
                uploadCategories(subSystemDocument, subSystemData, increment, dialog);

                //Save the sub system's immediate  data
                subSystemDocument.set(subSystemData.getSystemData())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.incrementProgress(increment);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.setStatusText(e.getMessage());
                            }
                        });

                //Upload the images in the sub system
                if(subSystemData.hasPictures())
                    uploadImage(dialog.getContext(), subSystemData.getPictures());

            }

            //Upload the category data
            uploadCategories(systemDocument, systemData, increment, dialog);

            //Save the system data
            systemDocument.set(systemData.getSystemData())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.incrementProgress(increment);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.setStatusText(e.getMessage());
                        }
                    });


        }

    }

    //Upload category documents into either a subsystem or MainSystem document.
    private void uploadCategories(DocumentReference systemDocument, InspectionData systemData, final double increment, ProgressDialog dialog)
    {

        //Loop through all Category Data and save each category in its own document
        for(Map<String, Object> categoryData : systemData.getCategoryData())
        {

            //Get the name of  the category
            Map<String, Object> categoryInfo = (HashMap<String, Object>) categoryData.get("Category Info");
            String type = (String) categoryInfo.get("Type");
            String name = (String) categoryInfo.get("Name");

            //Find the document where we will save the category data
            systemDocument.collection("Categories").document(name).set(categoryData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.incrementProgress(increment);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.setStatusText(e.getMessage());
                        }
                    });

        }

    }

    /*
    Upload PDF of the final inspection report
     */
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

    private static int queries = 0; // Compare received queries to the requested queries to know when to stop loading.
    private Map<String, Object> allSystemData;
    //Retrieve the saved system data from the database
    public Map<String, Object> fetchInspectionSystems(final ProgressDialog dialog)
    {

        //Get the schedule object of the inspection we're trying to load systems from
        Schedule schedule = Main.inspectionSchedule;

        //Create a HashMap to store the future system data
        allSystemData = new HashMap<>();

        //Get the inspection document, this document holds the following info:
        /*
        Address
        Client Information: First Name, Last Name
        Inspected Systems: ArrayList of all system display names
        Timestamp: StartDate, StartTime
         */
        //We're interested in the Inspected Systems here
        DocumentReference inspectionDocument =  getInspectionDocument(schedule);

        //Show the user we're accessing the inspection information
        dialog.setStatusText("Loading inspection...");

        /*
        Every System is loaded from the database into a Map

        Map<String, Object> systemData;
        key - "System" - will hold the data located in the System document itself
        key - "Data" - will contain another HashMap
            | key - "SubSystems" - will contain a HashMap of HashMaps for each SubSystem
                  | key - %SubSystemName%
                        | key - "System"  - contains the information of the SubSystem document itself
                        | key - "Data" - contains another HashMap
                              | key - "Categories" - contains a HashMap storing the data of each Category in the SubSystem
                                    | key -  %CategoryName% - contains the information of the category document
                              | key - "SubSystems" - should be empty, because a SubSystem has no SubSystems.
            | key - "Categories" - contains a HashMap storing the data of each Category in the System
                  | key - %CategoryName% - contains the category information of the category document itself

         */

        queries++;

        //Get the inspected systems from the "Systems" collection
        inspectionDocument.collection("Systems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {

                    queries--;

                    if(task.isSuccessful())
                    {

                        dialog.incrementProgress(1);

                        //Loop through every system and load its information
                        for(QueryDocumentSnapshot systemDocument : task.getResult())
                        {

                            Map<String, Object> systemData = new HashMap<>();

                            //Retrieve the data from the system document itself.
                            systemData.put("System", systemDocument.getData());

                            //Retrieve the internal system data.
                            systemData.put("Data",getSystemInformation(getSystemDocument(schedule, systemDocument.getId()), systemDocument.getId(), dialog));

                            allSystemData.put(systemDocument.getId(),systemData);

                        }

                        loadCheck( dialog);

                    }
                    else
                    {

                        //Cancel the loading process
                        dialog.setStatusText("No systems found.");
                        dialog.stopProgress();

                    }

                }
            });

        return allSystemData;

    }

    // Gets a Main System or Sub System information
    private Map<String, Object> getSystemInformation(DocumentReference systemDocument, String systemName, ProgressDialog dialog)
    {

        Schedule schedule = Main.inspectionSchedule;

        //HashMap stores all the data located in system file
        Map<String, Object> systemData = new HashMap<>();

        //Show the user we're now loading system information
        dialog.setStatusText("Loading " + systemName + " data...");

        //Now get the data in the system document
        //Every system has two COLLECTIONS:
        // Category collection
        // SubSystem collection
        // We need the various information from BOTH

        queries++;

        //Loop through all the subsystem documents in the system's subsystem collection
        systemDocument.collection("Sub Systems").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        queries--;

                        if(task.isSuccessful())
                        {
                            Map<String, Object> subSystems = new HashMap<>();

                            //Loop through all the found sub systems, these documents are guaranteed to exist
                            for(QueryDocumentSnapshot subSystemDocument : task.getResult())
                            {

                                Map<String, Object> subSystemData = new HashMap<>();

                                //Store the data of the sub system document itself
                                subSystemData.put("System", subSystemDocument.getData());

                                //Get the document reference of the subsystem document
                                DocumentReference document = getSystemDocument(schedule, systemName).collection("Sub Systems").document(subSystemDocument.getId());

                                //Get the data of the subsystem.
                                subSystemData.put("Data", getSystemInformation(document, subSystemDocument.getId(), dialog));

                                subSystems.put(subSystemDocument.getId(), subSystemData);

                            }

                            systemData.put("Sub Systems", subSystems);
                            loadCheck( dialog);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Failure here is expected when we're loading subsystem data. The SubSystem collection is never created.
                    }
                });

        queries++;

        //Now get the category data in the desired system
        systemDocument.collection("Categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {

                        queries--;
                        if (task.isSuccessful())
                        {

                            Map<String, Object> categories = new  HashMap<>();

                            for(QueryDocumentSnapshot categoryDocument : task.getResult())
                            {

                                //Store every category into the categories HashMap using the name of the category.
                                categories.put(categoryDocument.getId(), categoryDocument.getData());

                            }

                            //Store the category data into the system data
                            systemData.put("Categories", categories);

                            loadCheck( dialog);

                        }

                    }

                });

        return systemData;

    }

    private void loadCheck(ProgressDialog dialog)
    {

        if(queries == 0)
        {

            dialog.dismiss();
            Main.inspectionSchedule.inspection.finalizeLoadedInspection( dialog.getContext(), allSystemData);

        }


    }

    public void loadPastInspections(final Calendar date, boolean searchEntireMonth, final ArrayList<Schedule> pastInspections, Home homePage)
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
                                                    homePage.updatePastInspectionRecycler();

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

    private DocumentReference getSystemDocument(Schedule schedule, String systemName)
    {

        DocumentReference systemDocument = getInspectionDocument(schedule).collection("Systems").document(systemName);

        return systemDocument;

    }

}
