package daniel.walbolt.custominspections.Inspector.Dialogs.Informative;

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

public class DescriptionDialog extends Dialog implements View.OnClickListener
{

    private Button close;
    private TextView description;

    private Activity activity;

    //The description dialog is used to prompt the user with a message and a simple ok message.
    // This is a friendly way of knowing the default value of something, or the suggested value of something.

    //Most often used by the Comment Dialog, when first opened.

    public DescriptionDialog(@NonNull Context context, String message)
    {
        super(context);
        init(message);

    }

    private void init(String message)
    {

        setContentView(R.layout.alert_description);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.description = findViewById(R.id.dialog_description_description);
        this.description.setText(message);

        close = findViewById(R.id.dialog_description_close);
        close.setOnClickListener(this);

        show();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == close.getId())
        {

            dismiss();

        }

    }

}
