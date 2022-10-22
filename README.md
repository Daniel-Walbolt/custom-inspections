# custom-inspections

This android app was created to fulfill a business idea by my father. He used to be a residential Home Inspector, and knew about the money available in the service. However, instead of running a company, he thought he could create his own sole-proprietorship. This app was designed to allow him to create his own inspection process, but make it incredibly efficient, and located in one device. Scheduling, Inspecting, and Finalizing; from beginning to finish of every inspection, all on one app.

This app was a huge learning project, so hyper-efficient coding practices, or even architecture isn't perfect. The project was attempted to be organized using OOP. I utilized comments to explain the more complex classes.

This project is a continous learning project, and I think it is turning out fantastic despite not being 'up to standard.' This app is still a work-in-progress, but has a lot of core-functionality completed. Let's go over it!

The start of every inspection is the scheduling process, and this app handles it efficiently. 

![Schedules](https://user-images.githubusercontent.com/61804729/194942411-53a84155-293f-47eb-8e5c-6c0f6f2d4c57.gif)

Additionally, the user can remove a schedule in the case it gets cancelled, or the inspection is completed.

![RemoveSchedules](https://user-images.githubusercontent.com/61804729/194942503-53b4fd60-ce5c-4b62-bd17-19f8aa1d53f4.gif)

Upon opening the first-ever inspection, the user will be introduced to the "inspection-hub." This Activity provides access to the tools to inspect residences.
Without prior configuration, the custom-coded "Front" system is the only system available. There are also buttons to upload data and create the PDF; which we will come back to later.
Here is also a good place to demonstrate the app's ability to change its theme!

![FirstInspection](https://user-images.githubusercontent.com/61804729/194943671-f4852c6a-77db-4e1d-99f6-46384e405a72.gif)
![NightAndLightTheme](https://user-images.githubusercontent.com/61804729/194944063-a73d5344-258d-4aea-8fe5-8d2be3b166da.gif)

Systems will be mentioned a lot in this documentation, so it's important to clarify what they are. A system is a componenent of an inspection. Whether the inspection be for homes or businesses, a system represents an aspect of the property that needs to be inspected. In the final PDF systems are used as chapters, not unlike a book, to group together related information. This app allows the user to create systems whenever they want.

After the user creates a system, the app stores the data into SharedPreferences. As a result, every inspection henceforth will include that system in the "inspection-hub."

![SystemCreation](https://user-images.githubusercontent.com/61804729/195437912-bbccc3c7-787e-4553-a96c-4bda463fd367.gif)
![SystemConfigurationContinuity](https://user-images.githubusercontent.com/61804729/195437979-47653ecc-6a5c-4bbf-8d6a-697e97eede63.gif)

Now that we have created a system, it is automatically filled with default Categories. Categories are yet again another way of grouping related information. Much less complex than a System, but each Category groups together different kinds of information. Within the Information system, we can create 4 types of items: Check Box, Numeric Entry, Slider, or Group.

![CreatingCheckbox](https://user-images.githubusercontent.com/61804729/195696455-b20a04f1-0c6b-4c9a-b00d-dc679caa37f7.gif)
![CreatingASlider](https://user-images.githubusercontent.com/61804729/195696041-d9443ef5-8cd9-4448-87ce-707141623bec.gif)
![CreatingANumeric](https://user-images.githubusercontent.com/61804729/195696049-ac306660-7dd9-422a-82e0-9c8e3fe89b98.gif)
![CreatingAGroup](https://user-images.githubusercontent.com/61804729/195696324-f6e19a12-1fc2-4389-8db6-c30880f2a251.gif)

Unlike the Information category, the observations, restrictions, and defects only show 2 types of items, 1 being a Group. Their layouts are similar, but are used for very different purposes in an inspection.

![ShowcaseObservationsRestrictions Defects](https://user-images.githubusercontent.com/61804729/195698522-8f6714ce-c098-493b-8a1e-4d41d7688ace.gif)

Observations, Restrictions, and Defects sometimes require comments from the inspector to give more information. This app allows for comments on these types of items, and has a robust system to make boiler-plate comments quicker.

Global scope comments are comments that can be accessed throughout the entire inspection, in every system, for every item that has comments.

System scope comments are accessible for every item that has comments within the same system.

Section scope comments are accessible for only the item whose comments are being edited.

![CommentsShowcase](https://user-images.githubusercontent.com/61804729/195698717-33cf82ab-f6d1-48c8-bef7-664b56610005.gif)

The next category to talk about is the Sub System category. This category is very unique because it stores other "child" systems! This is another method of grouping together information, because when it comes to properties, there is a lot to talk about. Providing the tools to group information like this is crucial for making sure the screen doesn't get too crowded, and the final report is also easier to read. Sub-systems are the same as the System object we have been talking about with only a couple differences. For one, a Sub System does not have a Sub System category of its own, and this is to not make the inspection overly confusing; one nested system is enough. Secondly, in the final report, a Sub System will appear within the chapter of its "parent" system.

![SubSystemShowcase](https://user-images.githubusercontent.com/61804729/195702712-4c764e61-986f-41de-bd20-7c73f0c7f860.gif)

The final category in every system is the Settings category. This category is not editable by the user, and is hard-coded. This category provides basic tools to help the inspector during an inspection. Marking the system as partially complete will show the inspector that the system still needs more inspection. Likewise, marking a system as complete will signify to the inspection app that the system is done. A system can also be marked as excluded, which is equivalent to "complete" status, but means the system will not show up on the report. Some properties do not have every system configured in the app, so excluding systems is almost always going to happen. Finally, a system can be marked as a quality of the property to indicate to the client that this system is in excellent condition.

![SystemCompletionShowcase](https://user-images.githubusercontent.com/61804729/196004360-d16655a2-2ede-4881-b4ff-f4919987faf8.gif)
![SystemExclusionAndQualityShowcase](https://user-images.githubusercontent.com/61804729/196004364-020efa69-f2ba-442c-860c-7b5bf9acac21.gif)

After the inspector has finished entering all the applicable data for every system, the inspector can upload the data to the database. Uploading this data is crucial for any business to keep a record of what the inspector reported. If a client reaches out after an inspection, the inspector can reload the past inspection from the database to follow up on any questions/concerns.

Due to emulator problems, my custom camera-x implementation is unable to work. Due to this, the PDF can't be created with pictures using the emulator. However, I can show screenshots of it working on a physical device. Inspectors can take a picture very quickly inside the app by pressing the image icon on a category-item that has pictures. The picture is saved into temporary local storage, and the inspector is shown a preview of it. With this preview, the inspector can add a circle or square to the image, drag and resize these shapes, and put them over the image to indicate importance. I.e. for a defect, a circle on the image will help show where the defect exactly is in the image. After finishing editing, the inspector confirms the image, and the original image file is replaced with the the image including the shapes.

