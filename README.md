# custom-inspections

This android app was created to fulfill a business idea by my father. He used to be a residential Home Inspector, and knew about the money available in the service. However, instead of running a company, he thought he could create his own sole-proprietorship. This app was designed to allow him to create his own inspection process, but make it incredibly efficient, and located in one device. Scheduling, Inspecting, and Finalizing; from beginning to finish of every inspection, all on one app.

This app was a huge learning project, so hyper-efficient coding practices, or even architecture isn't perfect. The project was attempted to be organized using OOP. I utilized comments to explain the more complex classes.

This project is a continous learning project, and I think it is turning out fantastic despite not being 'up to standard.' This app is still a work-in-progress, but has a lot of core-functionality completed. Let's go over it!

The start of every inspection is the scheduling process, and this app handles it seamlessly. 

![Schedules](https://user-images.githubusercontent.com/61804729/194942411-53a84155-293f-47eb-8e5c-6c0f6f2d4c57.gif)

Additionally, the user can remove a schedule in the case it gets cancelled, or the inspection is completed.

![RemoveSchedules](https://user-images.githubusercontent.com/61804729/194942503-53b4fd60-ce5c-4b62-bd17-19f8aa1d53f4.gif)

Upon opening the first-ever inspection, the user will be introduced to the "inspection-hub." This Activity provides access to the tools to inspect residences.
Without prior configuration, the custom-coded "Front" system is the only system available. There are also buttons to upload data and create the PDF; which we will come back to later.
Here is also a good place to demonstrate the app's ability to change its theme!

![FirstInspection](https://user-images.githubusercontent.com/61804729/194943671-f4852c6a-77db-4e1d-99f6-46384e405a72.gif)
![NightAndLightTheme](https://user-images.githubusercontent.com/61804729/194944063-a73d5344-258d-4aea-8fe5-8d2be3b166da.gif)

Systems will be mentioned a lot in this documentation, so it's important to clarify what they are. A system is a category of inspection. Whether the inspection be for homes or businesses, a system represents an aspect of the property that needs to be inspected. In the final PDF systems are used as chapters, not unlike a book, to group together related information. This app allows the user to create systems whenever they want.

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

