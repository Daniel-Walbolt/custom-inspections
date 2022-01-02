package daniel.walbolt.custominspections.Inspector.Dialogs.Editors;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import daniel.walbolt.custominspections.Adapters.InspectionSystemAdapter;
import daniel.walbolt.custominspections.Inspector.Objects.System;
import daniel.walbolt.custominspections.R;

public class SystemsDialog extends Dialog
{

    private Activity mActivity;

    private boolean isSubSystem;
    private System parent;

    /*

    The Systems Dialog is opened from the Main inspection page and it prompts the user to create a new System.

     */

    public SystemsDialog(Activity activity, final InspectionSystemAdapter systemListAdapter, ArrayList<System> systemList, boolean isSubSystem, System parent)
    {

        super(activity);
        this.mActivity = activity;

        if(parent != null)
            this.parent = parent;

        this.isSubSystem = isSubSystem;

        //Initialize the view of the dialog
        setContentView(R.layout.systems_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);

        //Initialize the buttons of the dialog (cancel & confirm)
        initInteractables(systemList);

        //When the dialog is dismissed, reload the list of systems.
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                systemListAdapter.notifyDataSetChanged();
            }
        });

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

                //Add a new system to the system list
                systemList.add(new System(systemName.getText().toString(), parent));
                dismiss();

            }
        });

    }


}
