package com.steganomobile.common.sender.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.steganomobile.common.Const;
import com.steganomobile.common.R;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PrefsFileLockFragment.class.getName().equals(fragmentName)
                || PrefsFileSizeFragment.class.getName().equals(fragmentName)
                || PrefsGeneralFragment.class.getName().equals(fragmentName)
                || PrefsTypeOfIntentFragment.class.getName().equals(fragmentName)
                || PrefsUnixSocketFragment.class.getName().equals(fragmentName)
                || PrefsVolumeFragment.class.getName().equals(fragmentName);
    }

    public static class PrefsGeneralFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Const.PREF_KEY_INTERVAL)) {
                Preference connectionPref = findPreference(key);
                if (connectionPref != null) {
                    connectionPref.setSummary(sharedPreferences.getString(key, Const.DEFAULT_INTERVAL));
                }
            } else if (key.equals(Const.PREF_KEY_ITERATIONS)) {
                Preference connectionPref = findPreference(key);
                if (connectionPref != null) {
                    connectionPref.setSummary(sharedPreferences.getString(key, Const.DEFAULT_ITERATIONS));
                }
            } else if (key.equals(Const.PREF_KEY_CATEGORY_GLOBAL)) {
                Preference connectionPref = findPreference(key);
                if (connectionPref != null) {
                    connectionPref.setSummary(sharedPreferences.getString(key, " "));
                }
            } else if (key.equals(Const.PREF_KEY_CATEGORY_METHODS)) {
                Preference connectionPref = findPreference(key);
                if (connectionPref != null) {
                    connectionPref.setSummary(sharedPreferences.getString(key, " "));
                }
            } else if (key.equals(Const.PREF_KEY_METHODS)) {
                Preference connectionPref = findPreference(key);
                if (connectionPref != null) {
                    connectionPref.setSummary(sharedPreferences.getString(key, Const.DEFAULT_METHOD));
                }
            } else if (key.equals(Const.PREF_KEY_SYNC)) {
                Preference connectionPref = findPreference(key);
                if (connectionPref != null) {
                    connectionPref.setSummary(sharedPreferences.getString(key, Const.DEFAULT_SYNC));
                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_general);
        }
    }

    public static class PrefsTypeOfIntentFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_type_of_intent);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }

    public static class PrefsUnixSocketFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Const.PREF_KEY_PORT)) {
                Preference connectionPref = findPreference(key);
                if (connectionPref != null) {
                    Log.e(TAG, "Port changed");
                    connectionPref.setSummary(sharedPreferences.getString(key, Const.DEFAULT_PORT));
                }
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_unix_socket);
        }
    }

    public static class PrefsFileLockFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_file_lock);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }

    public static class PrefsFileSizeFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_file_size);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }

    public static class PrefsVolumeFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_volume);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    }
}