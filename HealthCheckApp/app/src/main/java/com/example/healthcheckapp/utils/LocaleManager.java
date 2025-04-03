package com.example.healthcheckapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.util.Locale;

public class LocaleManager {
    // SharedPreferences name and language key
    private static final String PREF_NAME = "settings";
    private static final String LANGUAGE_KEY = "language";

    // Set the language of the app and update resources accordingly
    public static void setLocale(Context context, String languageCode) {
        saveLanguage(context, languageCode);  // Save the selected language to SharedPreferences
        updateResources(context, languageCode);  // Update the app's resources to reflect the new language
    }

    // Get the currently selected language from SharedPreferences
    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(LANGUAGE_KEY, "fa");  // Default language is Persian ("fa")
    }

    // Save the language code to SharedPreferences
    private static void saveLanguage(Context context, String languageCode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(LANGUAGE_KEY, languageCode);  // Store the language code
        editor.apply();  // Apply the changes
    }

    // Update the app's resources with the new language
    private static void updateResources(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);  // Create a new Locale object with the selected language
        Locale.setDefault(locale);  // Set the default locale to the selected one
        Resources resources = context.getResources();  // Get the app's resources
        Configuration config = new Configuration(resources.getConfiguration());  // Create a new configuration based on current resources
        config.setLocale(locale);  // Set the locale in the configuration
        context.getResources().updateConfiguration(config, resources.getDisplayMetrics());  // Update the app's resources with the new configuration
    }
}
