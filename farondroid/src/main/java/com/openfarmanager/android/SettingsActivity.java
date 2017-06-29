package com.openfarmanager.android;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.openfarmanager.android.core.FontManager;
import com.openfarmanager.android.core.Settings;
import com.openfarmanager.android.filesystem.FileSystemScanner;
import com.openfarmanager.android.filesystem.actions.RootTask;
import com.openfarmanager.android.utils.StorageUtils;
import com.openfarmanager.android.utils.SystemUtils;
import com.openfarmanager.android.dialogs.FontSetupDialog;
import com.openfarmanager.android.dialogs.MarginSetupDialog;
import com.openfarmanager.android.view.ToastNotification;
import com.openfarmanager.android.view.YesNoPreference;

import java.util.HashMap;

import afzkl.development.colorpickerview.preference.ColorPickerPreference;
import afzkl.development.colorpickerview.view.ColorPickerView;

public class SettingsActivity extends PreferenceActivity {

    private Intent mResultData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResultData = new Intent(App.sInstance, SettingsActivity.class);
        setResult(Main.RESULT_SETTINGS_CHANGED, mResultData);

        addPreferencesFromResource(R.xml.preferences);

        Preference sdcardAccess = findPreference("sdcard_access");
        if (!StorageUtils.checkVersion()) {
            ((PreferenceCategory) findPreference("pref_key_file_system")).removePreference(findPreference("sdcard_access"));
        } else {
            sdcardAccess.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mResultData.putExtra(Main.RESULT_REQUEST_SDCARD_ACCEESS, true);
                    finish();
                    return true;
                }
            });
        }

        findPreference("file_system_sort").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showSingleChoiceDialog(R.layout.preference_list_view, getString(R.string.file_system_sort),
                        getResources().getStringArray(R.array.files_sort_array),
                        getResources().getStringArray(R.array.files_sort_array_values), App.sInstance.getSettings().getFileSortValue(),
                        new OnChooserValueSelectedListener() {
                            @Override
                            public void onValueSelected(int index) {
                                App.sInstance.getSettings().setFileSortValue(getResources().getStringArray(R.array.files_sort_array_values)[index]);
                            }
                        });
                return true;
            }
        });

        findPreference("file_system_file_info").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showSingleChoiceDialog(R.layout.preference_list_view, getString(R.string.file_system_file_info),
                        getResources().getStringArray(R.array.file_info_type_array),
                        getResources().getStringArray(R.array.file_info_type_array_values), App.sInstance.getSettings().getFileInfoType(),
                        new OnChooserValueSelectedListener() {
                            @Override
                            public void onValueSelected(int index) {
                                App.sInstance.getSettings().setFileInfoTypeValue(getResources().getStringArray(R.array.file_info_type_array_values)[index]);
                            }
                        });
                return true;
            }
        });

        findPreference("panel_font_size").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final Dialog dialog = new FontSetupDialog(SettingsActivity.this, new FontSetupDialog.SaveAction() {
                    @Override
                    public void execute(int newValue) {
                        App.sInstance.getSettings().setMainPanelFontSize(newValue);
                    }

                    @Override
                    public int getDefaultValue() {
                        return App.sInstance.getSettings().getMainPanelFontSize();
                    }
                });
                dialog.show();
                adjustDialogSize(dialog);
                return true;
            }
        });

        findPreference("panel_margin").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final Dialog dialog = new MarginSetupDialog(SettingsActivity.this, new FontSetupDialog.SaveAction() {
                    @Override
                    public void execute(int newValue) {
                        App.sInstance.getSettings().setMainPanelCellMargin(newValue);
                    }

                    @Override
                    public int getDefaultValue() {
                        return App.sInstance.getSettings().getMainPanelCellMargin();
                    }
                });
                dialog.show();
                adjustDialogSize(dialog);
                return true;
            }
        });

        findPreference("toolbar_font_size").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final Dialog dialog = new FontSetupDialog(SettingsActivity.this, new FontSetupDialog.SaveAction() {
                    @Override
                    public void execute(int newValue) {
                        if (newValue != getDefaultValue()) {
                            mResultData.putExtra(Main.RESULT_BOTTOM_PANEL_INVALIDATE, true);
                        }
                        App.sInstance.getSettings().setBottomPanelFontSize(newValue);
                    }

                    @Override
                    public int getDefaultValue() {
                        return App.sInstance.getSettings().getBottomPanelFontSize();
                    }
                });
                dialog.show();
                adjustDialogSize(dialog);
                return true;
            }
        });

        findPreference("viewer_font_size").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final Dialog dialog = new FontSetupDialog(SettingsActivity.this, new FontSetupDialog.SaveAction() {
                    @Override
                    public void execute(int newValue) {
                        App.sInstance.getSettings().setViewerFontSize(newValue);
                    }

                    @Override
                    public int getDefaultValue() {
                        return App.sInstance.getSettings().getViewerFontSize();
                    }
                });
                dialog.show();
                adjustDialogSize(dialog);
                return true;
            }
        });

        final HashMap<String, String> fonts = FontManager.enumerateFonts();
        if (fonts != null) {

            final String[] fontNames = new String[fonts.values().size()];
            fonts.values().toArray(fontNames);

            final String[] fontPathes = new String[fonts.keySet().size()];
            fonts.keySet().toArray(fontPathes);

            findPreference("panel_font").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    showSingleChoiceDialog(R.layout.preference_list_view, getString(R.string.main_panel_font),
                            fontNames,
                            fontPathes, App.sInstance.getSettings().getMainPanelFont(),
                            new OnChooserValueSelectedListener() {
                                @Override
                                public void onValueSelected(int index) {
                                    App.sInstance.getSettings().setMainPanelFont(fontPathes[index]);
                                    mResultData.putExtra(Main.RESULT_BOTTOM_PANEL_INVALIDATE, true);
                                }
                            }
                    );
                    return true;
                }
            });

            findPreference("viewer_font").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    showSingleChoiceDialog(R.layout.preference_list_view, getString(R.string.viewer_font),
                            fontNames,
                            fontPathes, App.sInstance.getSettings().getViewerFont(),
                            new OnChooserValueSelectedListener() {
                                @Override
                                public void onValueSelected(int index) {
                                    App.sInstance.getSettings().setViewerFont(fontPathes[index]);
                                }
                            }
                    );
                    return true;
                }
            });
        }

        final Settings settings = App.sInstance.getSettings();

        CheckBoxPreference multiPanels = (CheckBoxPreference) findPreference("multi_panels");
        multiPanels.setChecked(settings.isMultiPanelMode());
        CheckBoxPreference flexiblePanels = (CheckBoxPreference) findPreference("flexible_panels");
        flexiblePanels.setChecked(settings.isFlexiblePanelsMode());
        flexiblePanels.setEnabled(settings.isMultiPanelMode());

        CheckBoxPreference enableHomeFolder = (CheckBoxPreference) findPreference("enable_home_folder");
        enableHomeFolder.setChecked(settings.isEnableHomeFolder());
        Preference homeFolder = findPreference("home_folder");
        homeFolder.setEnabled(settings.isEnableHomeFolder());
        homeFolder.setSummary(settings.getHomeFolder());

        final ColorPickerPreference mainPanelColor = (ColorPickerPreference) findPreference("main_panel_color");
        mainPanelColor.setDefaultColor(settings.getMainPanelColor());
        mainPanelColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setMainPanelColor(newColor);
            }
        });

        final ColorPickerPreference secondaryColor = (ColorPickerPreference) findPreference("secondary_color");
        secondaryColor.setDefaultColor(settings.getSecondaryColor());
        secondaryColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setSecondaryColor(newColor);
                mResultData.putExtra(Main.RESULT_BOTTOM_PANEL_INVALIDATE, true);
            }
        });

        final ColorPickerPreference viewerColor = (ColorPickerPreference) findPreference("viewer_color");
        viewerColor.setDefaultColor(settings.getViewerColor());
        viewerColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setViewerColor(newColor);
            }
        });

        final ColorPickerPreference textColor = (ColorPickerPreference) findPreference("text_color");
        textColor.setDefaultColor(settings.getTextColor());
        textColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setTextColor(newColor);
            }
        });

        final ColorPickerPreference folderColor = (ColorPickerPreference) findPreference("folder_color");
        folderColor.setDefaultColor(settings.getFolderColor());
        folderColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setFolderColor(newColor);
            }
        });

        final ColorPickerPreference selectedColor = (ColorPickerPreference) findPreference("selected_color");
        selectedColor.setDefaultColor(settings.getSelectedColor());
        selectedColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setSelectedColor(newColor);
            }
        });

        final ColorPickerPreference hiddenColor = (ColorPickerPreference) findPreference("hidden_color");
        hiddenColor.setDefaultColor(settings.getHiddenColor());
        hiddenColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setHiddenColor(newColor);
            }
        });

        final ColorPickerPreference installColor = (ColorPickerPreference) findPreference("install_color");
        installColor.setDefaultColor(settings.getInstallColor());
        installColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setInstallColor(newColor);
            }
        });

        final ColorPickerPreference archiveColor = (ColorPickerPreference) findPreference("archive_color");
        archiveColor.setDefaultColor(settings.getArchiveColor());
        archiveColor.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
            @Override
            public void onColorChanged(int newColor) {
                settings.setArchiveColor(newColor);
            }
        });

        findPreference("feedback").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                sendFeedback();
                return true;
            }
        });

        findPreference("tips").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mResultData.putExtra(Main.RESULT_SHOW_HINT, true);
                App.sInstance.getSettings().getSharedPreferences().edit().putBoolean(Settings.SHOW_TIPS, true).commit();
                finish();
                return true;
            }
        });

        findPreference("rate").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse(getString(R.string.rate_app_url));
                Intent intent = new Intent (Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        });

        findPreference("reset_to_defaults").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final Dialog dialog = new YesNoPreference(SettingsActivity.this, getString(R.string.reset_to_defaults_summary),
                        new YesNoPreference.YesNoAction() {

                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            Settings settings = App.sInstance.getSettings();
                            try {
                                settings.resetStyle();
                            } catch (Exception ignore) {}
                            mResultData.putExtra(Main.RESULT_BOTTOM_PANEL_INVALIDATE, true);
                            mainPanelColor.setPreviewColor(settings.getMainPanelColor());
                            secondaryColor.setPreviewColor(settings.getSecondaryColor());
                            viewerColor.setPreviewColor(settings.getViewerColor());
                            textColor.setPreviewColor(settings.getTextColor());
                            folderColor.setPreviewColor(settings.getFolderColor());
                            selectedColor.setPreviewColor(settings.getSelectedColor());
                            hiddenColor.setPreviewColor(settings.getHiddenColor());
                            installColor.setPreviewColor(settings.getInstallColor());
                            archiveColor.setPreviewColor(settings.getArchiveColor());
                        }
                    }
                });
                dialog.show();
                adjustDialogSize(dialog);
                return true;
            }
        });

        App.sInstance.getSettings().getSharedPreferences().registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);
    }

    private void showSingleChoiceDialog(int layoutId, String title, final String[] items, final String[] values,
                                        String defaultValue, final OnChooserValueSelectedListener listener) {
        View view = getLayoutInflater().inflate(layoutId, null);

        final Dialog dialog = new Dialog(SettingsActivity.this, R.style.Action_Dialog);
        dialog.setContentView(view);

        RadioGroup group = (RadioGroup) view.findViewById(R.id.items_list);
        int i = 0;
        for (String item : items) {
            RadioButton button = new RadioButton(SettingsActivity.this);
            button.setButtonDrawable(R.drawable.radio_selector);
            button.setText(item);
            group.addView(button);

            button.setId(i);

            if (values[i++].equals(defaultValue)) {
                button.setChecked(true);
            }
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                listener.onValueSelected(i);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        ((TextView) view.findViewById(R.id.title)).setText(title);

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        adjustDialogSize(dialog);

    }

    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if ("folders_first".equals(s) || "sdcard_root".equals(s)) {
                FileSystemScanner.sInstance.initSorters();
            } else if ("multi_panels".equals(s)) {
                mResultData.putExtra(Main.RESULT_CODE_PANELS_MODE_CHANGED, true);
                SharedPreferences preferences = App.sInstance.getSettings().getSharedPreferences();
                if (!preferences.getBoolean(Settings.MULTI_PANELS_CHANGED, false)) {
                    preferences.edit().putBoolean(Settings.MULTI_PANELS_CHANGED, true).commit();
                }
                findPreference("flexible_panels").setEnabled(App.sInstance.getSettings().isMultiPanelMode());
            } else if ("enable_home_folder".equals(s)) {
                findPreference("home_folder").setEnabled(App.sInstance.getSettings().isEnableHomeFolder());
            } else if ("force_en_lang".equals(s)) {
                ToastNotification.makeText(getApplicationContext(), getString(R.string.will_be_applied_after_restarts),
                        Toast.LENGTH_LONG).show();
            } else if (Settings.ROOT_ENABLED.equals(s)){
                if (sharedPreferences.getBoolean(s, false)) {
                    if (!RootTask.requestRootsAccess()) {
                        sharedPreferences.edit().putBoolean(s, false).commit();
                        ToastNotification.makeText(getApplicationContext(), getString(R.string.root_not_found_or_denied),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    private void sendFeedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", getString(R.string.feedback_to), null));
        String version = "";
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pi.versionName + "/" + pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("Invalid package name", e);
        }

        String subject = getString(R.string.feedback_subject, version);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException anfe) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (!isFinishing())
                        Toast.makeText(SettingsActivity.this, getString(R.string.no_email_client), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        App.sInstance.getSettings().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mPreferenceChangeListener);
        super.onDestroy();
    }

    /**
     * Adjust dialog size. Actuall for old android version only (due to absence of Holo themes).
     */
    private void adjustDialogSize(Dialog dialog) {
        if (!SystemUtils.isHoneycombOrNever()) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(getWindow().getAttributes());
            params.width = (int) (metrics.widthPixels * 0.8f);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setAttributes(params);
        }
    }

    private interface OnChooserValueSelectedListener {
        void onValueSelected(int index);
    }

}
