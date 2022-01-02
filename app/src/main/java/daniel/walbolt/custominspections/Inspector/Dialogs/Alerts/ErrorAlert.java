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

public class ErrorAlert extends Dialog implements View.OnClickListener
{

    private Button ok;
    private TextView message;

    public ErrorAlert(@NonNull Context context, String message)
    {
        super(context);
        setContentView(R.layout.alert_error);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.message = findViewById(R.id.alert_message);
        this.message.setText(message);

        ok = findViewById(R.id.alert_confirm);
        ok.setOnClickListener(this);

        show();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.alert_confirm)
        {

            dismiss();

        }
    }

}
