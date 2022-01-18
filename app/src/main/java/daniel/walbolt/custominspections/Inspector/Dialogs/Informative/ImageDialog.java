package daniel.walbolt.custominspections.Inspector.Dialogs.Informative;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import daniel.walbolt.custominspections.Inspector.Objects.Other.InspectionMedia;
import daniel.walbolt.custominspections.R;

public class ImageDialog extends Dialog
{

    public ImageDialog(Context context, InspectionMedia media)
    {

        super(context);
        setContentView(R.layout.image);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);
        show();

        ImageView image = findViewById(R.id.dialog_image_picture);
        image.setImageURI(media.getURI(context));

        TextView resolution = findViewById(R.id.image_dialog_resolution);
        resolution.setText(image.getDrawable().getIntrinsicWidth() + "x" + image.getDrawable().getIntrinsicHeight());


    }

}
