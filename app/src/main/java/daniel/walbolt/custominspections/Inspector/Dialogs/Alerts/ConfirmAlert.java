package daniel.walbolt.custominspections.Inspector.Dialogs.Alerts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import daniel.walbolt.custominspections.R;

public class ConfirmAlert extends Dialog implements View.OnClickListener
{

    private Button yes;
    private Button no;
    private TextView message;
    private View.OnClickListener trueEvent; // The event to trigger when the user confirms the choice.
    private View.OnClickListener falseEvent; // The event to trigger when the user cancels the choice.

    private Activity activity;

    public ConfirmAlert(@NonNull Context context, String message)
    {
        super(context);
        init(message);

    }

    private void init(String message)
    {

        setContentView(R.layout.alert_confirm);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.message = findViewById(R.id.confirm_message);
        this.message.setText(message);

        yes = findViewById(R.id.confirm_yes);
        yes.setOnClickListener(this);

        no = findViewById(R.id.confirm_no);
        no.setOnClickListener(this);

        show();

    }

    public ConfirmAlert(Context context, String message, Activity closingActivity)
    {

        super(context);
        this.activity = closingActivity;
        init(message);

    }

    @Override
    public void onClick(View v)
    {

        if(v.getId() == R.id.confirm_yes)
        {

            dismiss();

            if(trueEvent != null)
                trueEvent.onClick(v);

            if(activity != null) {
                activity.finish();
            }
        }
        else if(v.getId() == R.id.confirm_no)
        {

            if (falseEvent != null)
                falseEvent.onClick(v);

            dismiss();

        }

    }

    public ConfirmAlert(Context context, String message, View.OnClickListener confirmEvent)
    {

        super(context);
        init(message);
        trueEvent = confirmEvent;

    }

    public ConfirmAlert(Context context, String message, View.OnClickListener confirmEvent, View.OnClickListener cancelEvent)
    {

        super(context);
        init(message);
        trueEvent = confirmEvent;
        falseEvent = cancelEvent;

    }

}
