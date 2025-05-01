package com.example.healthcheckapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TopScoresActivity extends AppCompatActivity {

    // UI Components
    private LinearLayout scoreListLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Data
    private static String lastLanguage = null;
    private JSONArray usersArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAppTheme();
        setContentView(R.layout.activity_topscores);

        initializeViews();
        setupNavigation();
        fetchDataFromServer();
    }

    /**
     * Sets up the app theme based on user preference (dark/light mode)
     */
    private void setupAppTheme() {
        if (ThemeManager.isDarkMode(this)) {
            setTheme(R.style.Theme_MyApp_Dark);
        } else {
            setTheme(R.style.Theme_MyApp_Light);
        }

        // Handle language settings
        String currentLang = LocaleManager.getLanguage(this);
        if (lastLanguage == null) {
            lastLanguage = currentLang;
        }
        LocaleManager.setLocale(this, currentLang);

        // Hide action bar if exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * Initializes all view components
     */
    private void initializeViews() {
        scoreListLayout = findViewById(R.id.scoresContainer);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Set card view background based on theme
        CardView cardView = findViewById(R.id.scoresCardView);
        cardView.setCardBackgroundColor(ThemeManager.isDarkMode(this) ?
                Color.parseColor("#0f4b6f") : Color.parseColor("#FFFFFF"));
    }

    /**
     * Sets up navigation drawer and its click listeners
     */
    private void setupNavigation() {
        // Open drawer when menu button is clicked
        findViewById(R.id.btnOpenMenu).setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.END));

        // Navigation item selection listener
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItemClick(item);
            return true;
        });
    }

    /**
     * Fetches high scores data from server in background thread
     */
    private void fetchDataFromServer() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;
            try {
                // Create connection to server
                URL url = new URL("https://b.mrbackend.ir/top_users.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000); // 15 seconds timeout
                conn.setReadTimeout(15000);

                // Check response code
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read response data
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    try {
                        // Parse JSON response
                        JSONArray jsonArray = new JSONArray(result.toString());
                        usersArray = jsonArray;
                        runOnUiThread(this::displayUserData);
                    } catch (JSONException e) {
                        runOnUiThread(() ->
                                Toast.makeText(this, "Error processing server data", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Server connection error. Code: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                // Clean up resources
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * Displays user data in the list
     */
    private void displayUserData() {
        try {
            scoreListLayout.removeAllViews();

            // Show empty state if no data
            if (usersArray == null || usersArray.length() == 0) {
                showEmptyState();
                return;
            }

            // Create a row for each user
            for (int i = 0; i < usersArray.length(); i++) {
                try {
                    JSONObject user = usersArray.getJSONObject(i);
                    String userId = user.optString("id", "");
                    String name = user.optString("name", "");
                    String surname = user.optString("surname", "");
                    int score = user.optInt("score", 0);

                    if (!userId.isEmpty()) {
                        createAndAddUserRow(userId, name, surname, score);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error displaying data", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates and adds a user row to the list
     */
    private void createAndAddUserRow(String userId, String name, String surname, int score) {
        try {
            // Create row layout
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 16, 0, 16);
            row.setTag(userId); // Store user ID as tag

            // Create text views for each column
            TextView tvId = createTextView(userId, 1);
            TextView tvName = createTextView(name + " " + surname, 2);
            TextView tvScore = createTextView(String.valueOf(score), 1);

            // Add views to row
            row.addView(tvId);
            row.addView(tvName);
            row.addView(tvScore);

            // Set up swipe to delete functionality
            setupSwipeToDelete(row);

            // Add row to main layout
            scoreListLayout.addView(row);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create a styled TextView
     */
    private TextView createTextView(String text, float weight) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, weight));
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        return textView;
    }

    /**
     * Sets up swipe gesture for deleting items
     */
    private void setupSwipeToDelete(LinearLayout row) {
        GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        // Detect left swipe
                        if (e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 100) {
                            confirmAndDeleteUser(row);
                            return true;
                        }
                        return false;
                    }
                });

        row.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    /**
     * Confirms and initiates user deletion
     */
    private void confirmAndDeleteUser(LinearLayout row) {
        try {
            String userId = (String) row.getTag();
            if (userId != null && !userId.isEmpty()) {
                new DeleteTask(row).execute(userId);
            } else {
                Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error processing deletion", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * AsyncTask for deleting user from server
     */
    private class DeleteTask extends AsyncTask<String, Void, Boolean> {
        private final LinearLayout row;
        private String errorMessage = "";

        DeleteTask(LinearLayout row) {
            this.row = row;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection conn = null;
            try {
                String userId = params[0];
                // Encode user ID for URL
                URL url = new URL("https://b.mrbackend.ir/delete_user.php?id=" + URLEncoder.encode(userId, "UTF-8"));
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                int responseCode = conn.getResponseCode();

                // Check server response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = reader.readLine();
                    reader.close();
                    return response != null && response.contains("success");
                }
                return false;
            } catch (Exception e) {
                errorMessage = e.getMessage();
                return false;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Animate deletion if successful
                animateDeletion(row);
                Toast.makeText(TopScoresActivity.this,
                        "User deleted successfully", Toast.LENGTH_SHORT).show();

                // Refresh data after 500ms delay
                new Handler().postDelayed(() -> fetchDataFromServer(), 500);
            } else {
                Toast.makeText(TopScoresActivity.this,
                        "Error deleting user: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Animates row deletion
     */
    private void animateDeletion(LinearLayout row) {
        try {
            row.animate()
                    .translationX(-row.getWidth()) // Slide to left
                    .alpha(0) // Fade out
                    .setDuration(300) // Animation duration
                    .withEndAction(() -> {
                        try {
                            scoreListLayout.removeView(row); // Remove after animation
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows empty state message
     */
    private void showEmptyState() {
        TextView emptyText = new TextView(this);
        emptyText.setText("No data available");
        emptyText.setTextSize(18);
        emptyText.setGravity(Gravity.CENTER);
        scoreListLayout.addView(emptyText);
    }

    /**
     * Handles navigation item clicks
     */
    private void handleNavigationItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_logout) {
            finishAffinity(); // Close app
            return;
        }

        Class<?> targetActivity = null;
        if (itemId == R.id.nav_register) {
            targetActivity = MainActivity.class;
        } else if (itemId == R.id.nav_login) {
            targetActivity = LoginActivity.class;
        } else if (itemId == R.id.nav_settings) {
            targetActivity = SettingsActivity.class;
        }

        if (targetActivity != null) {
            startActivity(new Intent(this, targetActivity));
        }

        // Clear selection after delay
        new Handler().postDelayed(() -> {
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }, 300);

        drawerLayout.closeDrawer(GravityCompat.END);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check for language changes
        String currentLang = LocaleManager.getLanguage(this);
        if (!currentLang.equals(lastLanguage)) {
            lastLanguage = currentLang;
            recreate(); // Recreate activity if language changed
        }
    }
}