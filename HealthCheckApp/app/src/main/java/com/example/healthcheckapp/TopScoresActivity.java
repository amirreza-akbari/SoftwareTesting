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
        // Create a background thread to fetch high scores from the server
        new Thread(() -> {
            try {
                // Server URL to fetch data
                URL url = new URL("https://b.mrbackend.ir/top_users.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);  // Connection timeout
                conn.setReadTimeout(5000);     // Read timeout
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new Exception("Server error: " + responseCode);
                }

                // Read the response from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                // Parse the response as a JSON array
                JSONArray usersArray = new JSONArray(result.toString());

                runOnUiThread(() -> {
                    // Clear existing views from the score list
                    scoreListLayout.removeAllViews();

                    // If no data is received, show a message
                    if (usersArray.length() == 0) {
                        TextView emptyText = new TextView(this);
                        emptyText.setText("No records found!");
                        emptyText.setTextSize(18);
                        emptyText.setGravity(Gravity.CENTER);
                        scoreListLayout.addView(emptyText);
                        return;
                    }

                    // Loop through the JSON array and display the records
                    for (int i = 0; i < usersArray.length(); i++) {
                        try {
                            JSONObject user = usersArray.getJSONObject(i);
                            String name = user.getString("name");    // Get name
                            String surname = user.getString("surname");  // Get surname
                            int score = user.optInt("score", 0);  // Get score (default to 0 if not found)

                            // Adjust name and surname based on the app's language
                            String currentLang = LocaleManager.getLanguage(this);
                            name = convertNameToLocal(name, currentLang);
                            surname = convertNameToLocal(surname, currentLang);

                            // Create a TextView to display each score record
                            TextView textView = new TextView(this);
                            textView.setText((i + 1) + ". " + name + " " + surname + " - " + (currentLang.equals("fa") ? "Score" : "Score") + ": " + score);
                            textView.setTextSize(18);
                            textView.setGravity(Gravity.CENTER);
                            textView.setPadding(16, 8, 16, 8);

                            // Change text color for rankings
                            if (i == 0) {
                                textView.setTextColor(Color.parseColor("#FFD700")); // Gold
                            } else if (i == 1) {
                                textView.setTextColor(Color.parseColor("#C0C0C0")); // Silver
                            } else if (i == 2) {
                                textView.setTextColor(Color.parseColor("#CD7F32")); // Bronze
                            } else {
                                textView.setTextColor(getResources().getColor(android.R.color.black));
                            }

                            // Set layout parameters and add to the layout
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(8, 8, 8, 8);
                            textView.setLayoutParams(params);

                            scoreListLayout.addView(textView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Server connection error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
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
