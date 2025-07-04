package com.example.coffee_shop.presentation.profile.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

    private static final String[] LANG_CODES = {"en", "uk", "pl"};
    private ProfileViewModel viewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        View v = getLayoutInflater().inflate(R.layout.dialog_language_change, null, false);

        RadioGroup group = v.findViewById(R.id.language_radio_group);
        v.findViewById(R.id.btn_cancel).setOnClickListener(c -> dismiss());
        v.findViewById(R.id.btn_apply).setOnClickListener(c -> apply(checkedIdToCode(group)));

        switch (savedLangIndex()) {
            case 1: group.check(R.id.radio_ukrainian); break;
            case 2: group.check(R.id.radio_polish);    break;
            default: group.check(R.id.radio_english);
        }

        return new AlertDialog.Builder(requireActivity()).setView(v).create();
    }

    private int savedLangIndex() {
        String saved = requireContext()
                .getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("language", "en");
        for (int i = 0; i < LANG_CODES.length; i++)
            if (LANG_CODES[i].equals(saved)) return i;
        return 0;
    }

    private String checkedIdToCode(RadioGroup group) {
        int id = group.getCheckedRadioButtonId();
        if (id == R.id.radio_ukrainian) return "uk";
        if (id == R.id.radio_polish)    return "pl";
        return "en";
    }

    private void apply(String code) {
        viewModel.updateAppLanguage(code);
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code));
        requireActivity().recreate();
        dismiss();
    }
}