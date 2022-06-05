package daniel.walbolt.custominspections.Inspector.Dialogs.Informative;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import daniel.walbolt.custominspections.Inspector.Pages.Main;
import daniel.walbolt.custominspections.R;

public class UploadDialog extends Dialog
{

    ProgressBar progressBar;
    TextView progressText;

    public UploadDialog(Context context)
    {
        super(context);
        setContentView(R.layout.upload);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initContent();

        show();

    }

    private void initContent()
    {

        Button cancel = findViewById(R.id.dialog_upload_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button confirm = findViewById(R.id.dialog_upload_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginUpload();
            }
        });

    }

    private void beginUpload()
    {

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dismissDialog();
            }
        });

        Main.inspectionSchedule.inspection.upload(progressDialog);

    }

    private void dismissDialog()
    {

        dismiss();

    }


}
