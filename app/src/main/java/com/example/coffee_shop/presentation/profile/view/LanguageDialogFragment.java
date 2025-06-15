package com.example.coffee_shop.presentation.profile.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coffee_shop.R;
import com.example.coffee_shop.presentation.profile.viewmodel.ProfileViewModel;

public class LanguageDialogFragment extends DialogFragment {

    private AlertDialog dialog;
    private ProfileViewModel profileViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        profileViewModel = new ViewModelProvider(requireActivity())
                .get(ProfileViewModel.class);

        View view = getLayoutInflater().inflate(
                R.layout.dialog_language_change, null, false);

        RadioGroup radioGroup   = view.findViewById(R.id.language_radio_group);
        Button cancelButton     = view.findViewById(R.id.btn_cancel);
        Button applyButton      = view.findViewById(R.id.btn_apply);

        // коди мов, що відображені у діалозі
        final String[] languageCodes = {"en", "uk", "pl"};

        // підсвічуємо вибрану мову
        switch (getCurrentLanguageIndex(languageCodes)) {
            case 1: radioGroup.check(R.id.radio_ukrainian); break;
            case 2: radioGroup.check(R.id.radio_polish);    break;
            default: radioGroup.check(R.id.radio_english);  break;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        dialog = builder.create();

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        applyButton.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            String lang;

            if (checkedId == R.id.radio_ukrainian) {
                lang = "uk";
            } else if (checkedId == R.id.radio_polish) {
                lang = "pl";
            } else {
                lang = "en";
            }
            applyLanguage(lang);
            dismiss();
        });

        return dialog;
    }

    /** Шукаємо, яку мову вже збережено у SharedPreferences */
    private int getCurrentLanguageIndex(String[] languageCodes) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String current = prefs.getString("language", "en");

        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(current)) return i;
        }
        return 0;
    }

    /** Зберігаємо вибір і застосовуємо його через AppCompatDelegate */
    private void applyLanguage(String langCode) {
        profileViewModel.updateAppLanguage(langCode);      // -> prefs

        AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(langCode));

        requireActivity().recreate();                     // оновити UI
    }
}