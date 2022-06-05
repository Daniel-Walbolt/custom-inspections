package daniel.walbolt.custominspections.Inspector.Dialogs.Informative;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import daniel.walbolt.custominspections.Inspector.Dialogs.Alerts.ConfirmAlert;
import daniel.walbolt.custominspections.R;

public class ProgressDialog extends Dialog
{

    ProgressBar mProgressBar;
    TextView progressText;
    Button cancel;

    /*
    The ProgressDialog is used by database behavior. Loading from the database, and uploading to the database reports status to the user.
    loading and uploading can not be cancelled by the user once started. However, if an error does occur,
    the user should be informed and the progress dialog should be cancellable.
     */

    public ProgressDialog(@NonNull Context context) {
        super(context);

        setContentView(R.layout.progress);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initContent();

        show();

    }

    @Override
    public void onBackPressed()
    {

        //You can not cancel the upload

    }

    private void initContent()
    {

        progressText = findViewById(R.id.dialog_progress_progressText);
        mProgressBar = findViewById(R.id.dialog_progress_progressBar);
        cancel = findViewById(R.id.dialog_progress_cancel);

    }

    public void incrementProgress(double progress)
    {

        mProgressBar.setProgress(mProgressBar.getProgress() + (int) progress, true);
        setStatusText(mProgressBar.getProgress() + "%");
        checkProgress();

    }

    private void checkProgress()
    {

        //Progress bar has a max progress of 100, but if we're loading an odd number of items, 99 is a possible fraction (int)(100/3)*3
        if(mProgressBar.getProgress() >= 99)
            dismiss();

    }

    //Set the status of the upload/load. This is how  an error is displayed
    public void setStatusText(String message)
    {

        progressText.setText(message);

    }

    //Let the dialog know that the progress will no longer be updated.
    // Await user input to close the dialog.
    public void stopProgress()
    {

        setCanceledOnTouchOutside(true);

    }

}
