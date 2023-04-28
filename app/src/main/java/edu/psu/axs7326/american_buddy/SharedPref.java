package edu.psu.axs7326.american_buddy;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private SharedPreferences sharedPreferences;

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    //Save theme preference
    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }

    //Get theme preference
    public Boolean loadNightModeState() {
        return sharedPreferences.getBoolean("NightMode", false);
    }

    public void setDisplay(Boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("default", state);
        editor.apply();
    }

    //Get swap preference
    public Boolean loadDisplay() {
        return sharedPreferences.getBoolean("default", false);
    }


}
