package com.example.healthcheckapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeManager {
    // Name of the SharedPreferences file
    private static final String PREF_NAME = "theme_prefs";
    // Key to store the selected theme preference
    private static final String KEY_THEME = "selected_theme";

    // Method to save the theme preference (dark mode or light mode)
    public static void saveTheme(Context context, boolean isDarkMode) {
        // Access SharedPreferences for storing the theme setting
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Open SharedPreferences editor to modify the settings
        SharedPreferences.Editor editor = prefs.edit();
        // Save the boolean value indicating whether dark mode is enabled
        editor.putBoolean(KEY_THEME, isDarkMode);
        // Apply changes
        editor.apply();
    }

    // Method to check if dark mode is enabled
    public static boolean isDarkMode(Context context) {
        // Access SharedPreferences to retrieve the stored theme setting
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Return the stored value; default is false (light mode)
        return prefs.getBoolean(KEY_THEME, false);
    }
}
