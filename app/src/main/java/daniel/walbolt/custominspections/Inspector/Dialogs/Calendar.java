package daniel.walbolt.custominspections.Inspector.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class Calendar extends Dialog
{

    public Calendar(Activity activity)
    {

        super(activity);

        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

}
