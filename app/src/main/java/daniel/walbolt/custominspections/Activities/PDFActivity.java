package daniel.walbolt.custominspections.Activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import daniel.walbolt.custominspections.MainActivity;
import daniel.walbolt.custominspections.PDF.PDFPreview;

public class PDFActivity extends MainActivity
{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        new PDFPreview(this);

    }

}