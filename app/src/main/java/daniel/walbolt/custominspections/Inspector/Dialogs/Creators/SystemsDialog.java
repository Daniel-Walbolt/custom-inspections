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
import daniel.walbolt.custominspections.R;

public class SystemsDialog extends Dialog
{

    private boolean isSubSystem;
    private System parent;

    private ArrayList<System> systemList;

    /*

    The Systems Dialog is opened from the Main inspection page and it prompts the user to create a new System.

     */

    public SystemsDialog(Context context, ArrayList<System> systemList, System parent)
    {

        super(context);

        this.parent = parent;
        this.systemList = systemList;
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

                checkName(systemName);
                //Add a new system to the system list
                systemList.add(new System(systemName.getText().toString(), parent));
                dismiss();

            }
        });

    }


    private boolean checkName(EditText systemName)
    {

        if(systemName.getText().toString().isEmpty())
        {

            new ErrorAlert(getContext(), "System name can not be empty!");
            return false;

        }
        else
        {

            //If the system name is valid, loop through the systems already in the target list
            for(System system : systemList)
            {

                if(system.getDisplayName().equalsIgnoreCase(systemName.getText().toString()))
                {

                    new ErrorAlert(getContext(), "Another system already has that name!");

                    return false;

                }

            }

            return true;

        }

    }

}
