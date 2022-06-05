package daniel.walbolt.custominspections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import daniel.walbolt.custominspections.Inspector.Pages.Home;

public class MainActivity extends AppCompatActivity
{

    public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
    public static final String NIGHT_MODE = "NIGHT_MODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        //Set the theme of the app according to the shared preference
        if(sharedPreferences.getBoolean(NIGHT_MODE, false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        new Home(this);

    }
}