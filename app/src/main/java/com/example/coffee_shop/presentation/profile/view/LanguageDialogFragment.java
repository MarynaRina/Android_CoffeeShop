package com.example.coffee_shop.presentation.profile.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop.R;
import com.example.coffee_shop.presentation.profile.viewmodel.ProfileViewModel;

import java.util.Locale;

public class LanguageDialogFragment extends DialogFragment {
    private AlertDialog dialog;
    private ProfileViewModel profileViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get the ProfileViewModel directly from the parent activity
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Inflate custom layout
        View view = getLayoutInflater().inflate(R.layout.dialog_language_change, null);

        RadioGroup radioGroup = view.findViewById(R.id.language_radio_group);
        Button cancelButton = view.findViewById(R.id.btn_cancel);
        Button applyButton = view.findViewById(R.id.btn_apply);

        // Language options
        final String[] languageCodes = {"en", "uk", "зд"};

        // Set the currently selected language
        int currentLanguageIndex = getCurrentLanguageIndex(languageCodes);
        switch (currentLanguageIndex) {
            case 0:
                radioGroup.check(R.id.radio_english);
                break;
            case 1:
                radioGroup.check(R.id.radio_ukrainian);
                break;
            case 2:
                radioGroup.check(R.id.radio_polish);
                break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);

        dialog = builder.create();

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        applyButton.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            String languageCode;

            if (selectedId == R.id.radio_english) {
                languageCode = "en";
            } else if (selectedId == R.id.radio_ukrainian) {
                languageCode = "uk";
            } else if (selectedId == R.id.radio_polish) {
                languageCode = "pl";
            } else {
                languageCode = "en";
            }

            updateAppLanguage(languageCode);
            dialog.dismiss();
        });

        return dialog;
    }

    private int getCurrentLanguageIndex(String[] languageCodes) {
        // Get current language from shared preferences or default to English (0)
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String currentLang = prefs.getString("language", "en");

        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                return i;
            }
        }
        return 0; // Default to English
    }

    private void updateAppLanguage(String languageCode) {
        profileViewModel.updateAppLanguage(languageCode);

        // Update locale
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources res = requireActivity().getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);

        res.updateConfiguration(config, res.getDisplayMetrics());

        // Restart the activity to apply changes
        Intent refresh = new Intent(requireActivity(), requireActivity().getClass());
        requireActivity().finish();
        startActivity(refresh);
    }
}