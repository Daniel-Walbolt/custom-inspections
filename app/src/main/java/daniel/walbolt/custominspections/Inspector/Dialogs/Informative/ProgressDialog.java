package daniel.walbolt.custominspections.Inspector.Dialogs.Informative;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import daniel.walbolt.custominspections.R;

public class ProgressDialog extends Dialog
{

    ProgressBar mProgressBar;
    TextView progressText;

    public ProgressDialog(@NonNull Context context) {
        super(context);

        setContentView(R.layout.progress);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initContent();

        show();

    }

    private void initContent()
    {

        progressText = findViewById(R.id.dialog_upload_progressText);;

    }

    public void incrementProgress(int progress)
    {

        mProgressBar.setProgress(mProgressBar.getProgress() + progress, true);
        setStatusText(mProgressBar.getProgress() + "%");
        checkProgress();

    }

    private void checkProgress()
    {

        //Progress bar has a max progress of 100, but if we're loading an odd number of items, 99 is a possible fraction (int)(100/3)*3
        if(mProgressBar.getProgress() >= 99)
            dismiss();

    }

    public void setStatusText(String message)
    {

        progressText.setText(message);

    }

}
