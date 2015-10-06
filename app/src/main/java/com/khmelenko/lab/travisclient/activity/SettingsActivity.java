package com.khmelenko.lab.travisclient.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.khmelenko.lab.travisclient.R;
import com.khmelenko.lab.travisclient.common.Constants;
import com.khmelenko.lab.travisclient.storage.AppSettings;

/**
 * Settings activity
 *
 * @author Dmytro Khmelenko
 */
public class SettingsActivity extends PreferenceActivity {

    private static final String SERVER_TYPE_KEY = "settings_server_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            ListPreference serverTypeSetting = (ListPreference) findPreference(SERVER_TYPE_KEY);
            serverTypeSetting.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Integer selectedItem = Integer.valueOf(newValue.toString());
                    switch (selectedItem) {
                        case 1:
                            AppSettings.putServerType(Constants.OPEN_SOURCE_TRAVIS_URL);
                            break;
                        case 2:
                            AppSettings.putServerType(Constants.PRIVATE_TRAVIS_URL);
                            break;
                        case 3:
                            showInputDialog();
                            break;
                    }
                    return true;
                }
            });
        }

        private void showInputDialog() {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(getActivity());
            View promptsView = li.inflate(R.layout.prompt_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getActivity());

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            TextView promptIntro = (TextView) promptsView.findViewById(R.id.prompt_dialog_intro);
            promptIntro.setText(R.string.settings_setver_custom_input_text);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.prompt_dialog_input);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.settings_server_input_btn_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String serverAdress = userInput.getText().toString();
                                    if (!TextUtils.isEmpty(serverAdress)) {
                                        AppSettings.putServerType(serverAdress);
                                    } else {
                                        userInput.setError(getString(R.string.settings_server_input_error_empty));
                                    }
                                }
                            })
                    .setNegativeButton(getString(R.string.settings_server_input_btn_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }

}
