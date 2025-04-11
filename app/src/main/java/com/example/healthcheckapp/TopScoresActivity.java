package com.example.healthcheckapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.example.healthcheckapp.utils.LocaleManager;
import com.example.healthcheckapp.utils.ThemeManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;
import android.os.Looper;

public class TopScoresActivity extends AppCompatActivity {

    LinearLayout scoreListLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static String lastLanguage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme based on dark or light mode
        if (ThemeManager.isDarkMode(this)) {
            setTheme(R.style.Theme_MyApp_Dark);
        } else {
            setTheme(R.style.Theme_MyApp_Light);
        }

        String currentLang = LocaleManager.getLanguage(this);
        if (lastLanguage == null) {
            lastLanguage = currentLang;
        }
        // Set the app locale to the current language
        LocaleManager.setLocale(this, currentLang);

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topscores);

        // Initialize views
        CardView cardView = findViewById(R.id.scoresCardView); // Get the CardView ID
        scoreListLayout = findViewById(R.id.scoresContainer);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Open the drawer when the button is clicked
        findViewById(R.id.btnOpenMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        // Set listener for navigation menu items
        navigationView.setNavigationItemSelectedListener(this::handleNavigation);

        // Call method to fetch high scores
        getHighScores();

        // Change CardView background color based on the theme
        if (ThemeManager.isDarkMode(this)) {
            cardView.setCardBackgroundColor(Color.parseColor("#0f4b6f")); // Dark blue for dark mode
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF")); // White for light mode
        }
    }

    private void getHighScores() {
        new Thread(() -> {
            try {
                URL url = new URL("https://b.mrbackend.ir/top_users.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) throw new Exception("Server error: " + responseCode);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);
                reader.close();

                JSONArray usersArray = new JSONArray(result.toString());

                runOnUiThread(() -> {
                    scoreListLayout.removeAllViews();

                    if (usersArray.length() == 0) {
                        TextView emptyText = new TextView(this);
                        emptyText.setText("No records found!");
                        emptyText.setTextSize(18);
                        emptyText.setGravity(Gravity.CENTER);
                        scoreListLayout.addView(emptyText);
                        return;
                    }

                    for (int i = 0; i < usersArray.length(); i++) {
                        try {
                            JSONObject user = usersArray.getJSONObject(i);
                            String name = user.getString("name");
                            String surname = user.getString("surname");
                            int score = user.optInt("score", 0);
                            String fullName = convertNameToLocal(name + " " + surname, LocaleManager.getLanguage(this));

                            // Row (LinearLayout)
                            LinearLayout row = new LinearLayout(this);
                            row.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            row.setOrientation(LinearLayout.HORIZONTAL);
                            row.setPadding(0, 16, 0, 16);

                            // Column ID
                            TextView tvId = new TextView(this);
                            tvId.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            tvId.setText(String.valueOf(i + 1));
                            tvId.setGravity(Gravity.CENTER);
                            tvId.setTextSize(16);

                            // Column name
                            TextView tvName = new TextView(this);
                            tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                            tvName.setText(fullName);
                            tvName.setGravity(Gravity.CENTER);
                            tvName.setTextSize(16);

                            // Column score
                            TextView tvScore = new TextView(this);
                            tvScore.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            tvScore.setText(String.valueOf(score));
                            tvScore.setGravity(Gravity.CENTER);
                            tvScore.setTextSize(16);

                            // Add columns to rows
                            row.addView(tvId);
                            row.addView(tvName);
                            row.addView(tvScore);

                            // Add row to main list
                            scoreListLayout.addView(row);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }


    // Method to convert names to the local language
    private String convertNameToLocal(String text, String currentLang) {
        // Assuming names stay the same in both languages, but translation can be added here if needed
        return text;
    }

    // Handle navigation menu item selection
    private boolean handleNavigation(MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;

        if (itemId == R.id.nav_register) {
            intent = new Intent(TopScoresActivity.this, MainActivity.class);
        } else if (itemId == R.id.nav_login) {
            intent = new Intent(TopScoresActivity.this, LoginActivity.class);
        } else if (itemId == R.id.nav_settings) {
            intent = new Intent(TopScoresActivity.this, SettingsActivity.class);
        } else if (itemId == R.id.nav_logout) {
            finishAffinity();  // Exit the app
            return true;
        }

        if (intent != null) {
            startActivity(intent);
        }

        // Deselect all items after a slight delay
        new Handler().postDelayed(() -> {
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }, 300);

        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if language has changed, and recreate the activity if necessary
        String currentLang = LocaleManager.getLanguage(this);
        if (!currentLang.equals(lastLanguage)) {
            lastLanguage = currentLang;
            recreate();
        }
    }
}
