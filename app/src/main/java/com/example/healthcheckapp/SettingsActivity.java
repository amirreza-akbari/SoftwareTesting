package com.example.healthcheckapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.healthcheckapp.utils.LocaleManager;
import com.example.healthcheckapp.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the language before displaying the UI
        LocaleManager.setLocale(this, LocaleManager.getLanguage(this));

        // Set the theme (dark or light) before displaying the activity
        if (ThemeManager.isDarkMode(this)) {
            setTheme(R.style.Theme_MyApp_Dark);
        } else {
            setTheme(R.style.Theme_MyApp_Light);
        }

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get the CardView ID for styling
        CardView cardView = findViewById(R.id.cardView);

        // Set the background color of the card based on the theme
        if (ThemeManager.isDarkMode(this)) {
            cardView.setCardBackgroundColor(Color.parseColor("#0f4b6f")); // Navy in dark mode
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF")); // White in light mode
        }

        // Initialize the theme switch (dark mode toggle)
        Switch switchTheme = findViewById(R.id.switch_theme);
        switchTheme.setChecked(ThemeManager.isDarkMode(this));
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save the selected theme and restart the app
            ThemeManager.saveTheme(this, isChecked);
            restartApp();
        });

        // Initialize the language selection spinner
        Spinner spinnerLanguage = findViewById(R.id.spinner_language);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        // Set the default value for the language spinner based on the saved language
        String currentLanguage = LocaleManager.getLanguage(this);
        spinnerLanguage.setSelection(currentLanguage.equals("fa") ? 0 : 1);

        // Listener for when a language is selected
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Change language based on selection and recreate the activity
                String selectedLanguage = (position == 0) ? "fa" : "en";
                if (!selectedLanguage.equals(LocaleManager.getLanguage(SettingsActivity.this))) {
                    LocaleManager.setLocale(SettingsActivity.this, selectedLanguage);
                    recreate(); // Restart the settings activity
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initialize the "Back to Main" button
        MaterialButton btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(v -> {
            finish(); // Close this activity and return to the previous screen
        });
    }

    // Method to restart the app after changing theme
    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity to prevent it from staying in the stack
    }
}
