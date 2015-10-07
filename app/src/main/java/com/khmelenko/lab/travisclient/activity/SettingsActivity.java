package com.khmelenko.lab.travisclient.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.network.retrofit.RestClient;
import com.khmelenko.lab.travisclient.storage.AppSettings;

/**
 * Settings activity
 *
 * @author Dmytro Khmelenko
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String SETTINGS_SCREEN_KEY = "settings_screen";
    private static final String SERVER_TYPE_KEY = "settings_server_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();

        getFragmentManager().beginTransaction().replace(R.id.settings_content, new SettingsFragment()).commit();
    }

    /**
     * Initializes toolbar
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String server = AppSettings.getServerUrl();
        RestClient.getInstance().updateTravisEndpoint(server);
    }

    public static class SettingsFragment extends PreferenceFragment {

        private ListPreference mServerTypeSetting;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            mServerTypeSetting = (ListPreference) findPreference(SERVER_TYPE_KEY);
            updateServerSummary(AppSettings.getServerType());
            mServerTypeSetting.setValue(String.valueOf(AppSettings.getServerType()));
            mServerTypeSetting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Integer selectedItem = Integer.valueOf(newValue.toString());
                    updateServerSummary(selectedItem);
                    switch (selectedItem) {
                        case 0:
                            AppSettings.putServerUrl(Constants.OPEN_SOURCE_TRAVIS_URL);
                            AppSettings.putServerType(0);
                            break;
                        case 1:
                            AppSettings.putServerUrl(Constants.PRIVATE_TRAVIS_URL);
                            AppSettings.putServerType(1);
                            break;
                        case 2:
                            showInputDialog();
                            break;
                    }
                    return true;
                }
            });
        }

        /**
         * Updates the summary for the server setting
         *
         * @param serverType Server type
         */
        private void updateServerSummary(int serverType) {
            String[] items = getResources().getStringArray(R.array.settings_server_types);
            switch (serverType) {
                case 0:
                    AppSettings.putServerType(0);
                    mServerTypeSetting.setSummary(items[0]);
                    break;
                case 1:
                    AppSettings.putServerType(1);
                    mServerTypeSetting.setSummary(items[1]);
                    break;
                case 2:
                    String server = AppSettings.getServerUrl();
                    mServerTypeSetting.setSummary(server);
                    break;
            }
        }

        /**
         * Shows input dialog
         */
        private void showInputDialog() {
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.dialog_prompt, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());
            alertDialogBuilder.setView(promptsView);

            TextView promptIntro = (TextView) promptsView.findViewById(R.id.prompt_dialog_intro);
            promptIntro.setText(R.string.settings_setver_custom_input_text);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.prompt_dialog_input);
            userInput.requestFocus();

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.settings_server_input_btn_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String serverAddress = userInput.getText().toString();
                                    AppSettings.putServerUrl(serverAddress);
                                    AppSettings.putServerType(2);
                                    mServerTypeSetting.setSummary(serverAddress);
                                }
                            })
                    .setNegativeButton(getString(R.string.settings_server_input_btn_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();

                                    // restore previous selection
                                    mServerTypeSetting.setValue(String.valueOf(AppSettings.getServerType()));
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }

}
