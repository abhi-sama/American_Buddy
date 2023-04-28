package edu.psu.axs7326.american_buddy;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public static class SettingsFragment extends PreferenceFragment {

        private SharedPref sharedPreferences;
        private CheckBoxPreference toggleTheme;

        private CheckBoxPreference toggle_display;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            sharedPreferences = new SharedPref(getActivity().getApplicationContext());
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            toggleTheme = (CheckBoxPreference) findPreference("dark");
            if (sharedPreferences.loadNightModeState()) {
                toggleTheme.isEnabled();
            }

            toggleTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isChecked = (boolean) newValue;
                    if (isChecked) {
                        sharedPreferences.setNightModeState(true);
                    } else {
                        sharedPreferences.setNightModeState(false);
                    }
                    Toast.makeText(getActivity(),"Theme changed. Please Restart the App",Toast.LENGTH_LONG).show();
                    return true;
                }
            });

            toggle_display = (CheckBoxPreference) findPreference("swap");
            if (sharedPreferences.loadDisplay()) {
                toggleTheme.isEnabled();
            }

            toggle_display.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isChecked = (boolean) newValue;
                    if (isChecked) {
                        sharedPreferences.setDisplay(true);
                    } else {
                        sharedPreferences.setDisplay(false);
                    }
                   Toast.makeText(getActivity(),"Please Restart the App to apply the changes.",Toast.LENGTH_LONG).show();
                    return true;
                }
            });


            }

        }
        }



