package daniel.walbolt.custominspections.Inspector.Dialogs.Creators;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.InspectionSystemAdapter;
import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ErrorAlert;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.R;

public class SystemsDialog extends Dialog
{

    private boolean isSubSystem;
    private System parent;

    private ArrayList<System> systemList;

    /*

   The Systems Dialog prompts the user to enter a System name.
    If the name is unique to the new system, a new system will be created and added to the list provided in the constructor.
     */

    public SystemsDialog(Context context, System parent)
    {

        super(context);

        this.parent = parent;
        if (parent != null)
            this.systemList = parent.getSubSystems();

        isSubSystem = true;

        //Initialize the view of the dialog
        setContentView(R.layout.systems_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);

        //Initialize the buttons of the dialog (cancel & confirm)
        initInteractables(systemList);

        show();

    }

    private void initInteractables(ArrayList<System> systemList)
    {

        EditText systemName = findViewById(R.id.create_system_name);

        //If the user confirms the creation of a system, there are a couple options
        Button confirm = findViewById(R.id.create_system_confirm);
        confirm.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                if (checkName(systemName))
                {

                    //If the system list is not null, then the system list is a subsystem list.
                    if (systemList != null)
                        systemList.add(new System(systemName.getText().toString(), parent)); // Add the system to the target list.
                    else // Otherwise, the target system list is the user-created Main System list. In this case, I need to call a method to add the system.
                        Main.inspectionSchedule.inspection.addUserSystem(new System(systemName.getText().toString(), parent));

                    dismiss();

                }

            }
        });

    }


    private boolean checkName(EditText systemName)
    {

        // The system the user is creating can not have a blank name.
        if(systemName.getText().toString().isEmpty())
        {

            new ErrorAlert(getContext(), "System name can not be empty!");
            return false;

        }
        else if (systemList == null) // The new system is being added to the Main System list
            systemList = Main.inspectionSchedule.inspection.getAllSystems();

        //The system name is valid, loop through the systems already in the target list
        for(System system : systemList)
        {

            //If the system has the same spelling as any other system's name in the list, return false.
            if(system.getDisplayName().equalsIgnoreCase(systemName.getText().toString()))
            {

                new ErrorAlert(getContext(), "Another system already has that name!");

                return false;

            }

        }

        return true;

    }

}
