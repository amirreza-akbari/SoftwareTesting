package com.example.healthcheckapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.healthcheckapp.utils.LocaleManager;
import com.example.healthcheckapp.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Looper;


public class LoginActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private static String lastLanguage = null;

    private String wait, emptyFieldsMessage, invalidEmailMessage, saveSuccessMessage, saveErrorMessage, serverErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the theme based on dark mode preference
        if (ThemeManager.isDarkMode(this)) {
            setTheme(R.style.Theme_MyApp_Dark);
        } else {
            setTheme(R.style.Theme_MyApp_Light);
        }

        // Get the current language and set it
        String currentLang = LocaleManager.getLanguage(this);
        if (lastLanguage == null) {
            lastLanguage = currentLang;
        }
        LocaleManager.setLocale(this, currentLang);
        setMessagesBasedOnLanguage(currentLang);

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set up the login activity's layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CardView cardView = findViewById(R.id.cardView);

        // Set card background color based on the theme
        if (ThemeManager.isDarkMode(this)) {
            cardView.setCardBackgroundColor(Color.parseColor("#0f4b6f"));
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        // Initialize the UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Set up the side menu button
        findViewById(R.id.btnOpenMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(this::handleNavigation);

        // Hide login item in the navigation menu
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_login).setVisible(false);

        // Handle login button click
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Check if fields are empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, emptyFieldsMessage, Toast.LENGTH_SHORT).show();
            } else {
                // Call the login API if fields are filled
                loginUser(email, password);
            }
        });
    }

    // Method to handle the login process
    private void loginUser(String email, String password) {
        String url = "https://b.mrbackend.ir/login.php";

        // Set up a request to the server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d("LoginResponse", response);

                        // Parse the server response
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        // If login is successful, navigate to the next activity
                        if (status.equals("success")) {
                            String name = jsonResponse.getString("name");
                            String surname = jsonResponse.getString("surname");
                            String userEmail = jsonResponse.getString("email");
                            String score = jsonResponse.getString("score");

                            // Display success message
                            Toast.makeText(LoginActivity.this, saveSuccessMessage, Toast.LENGTH_SHORT).show();

                            // Start the result activity with the user data
                            Intent intent = new Intent(LoginActivity.this, ResultActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("surname", surname);
                            intent.putExtra("email", userEmail);
                            intent.putExtra("score", score);
                            startActivity(intent);
                        } else {
                            // Show error message if login fails
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, serverErrorMessage, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(LoginActivity.this, serverErrorMessage, Toast.LENGTH_SHORT).show()
        ) {
            // Add parameters to the POST request
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // Add the request to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // Handle navigation menu item selection
    private boolean handleNavigation(MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;

        // Navigate to the corresponding activity based on selected menu item
        if (itemId == R.id.nav_home) {
            intent = new Intent(LoginActivity.this, TopScoresActivity.class);
        } else if (itemId == R.id.nav_register) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        } else if (itemId == R.id.nav_settings) {
            intent = new Intent(LoginActivity.this, SettingsActivity.class);
        } else if (itemId == R.id.nav_logout) {
            finishAffinity();  // Close all activities and exit
            return true;
        }

        // Start the selected activity if it's not null
        if (intent != null) {
            startActivity(intent);
        }

        // Deselect all menu items after a delay to improve user experience
        new Handler().postDelayed(() -> {
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }, 300);

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    // Set messages based on the selected language
    private void setMessagesBasedOnLanguage(String language) {
        if (language.equals("fa")) {
            wait = "لطفا صبرکنید...";
            emptyFieldsMessage = "لطفا تمام فیلدها را پر کنید";
            invalidEmailMessage = "ایمیل وارد شده معتبر نیست";
            saveSuccessMessage = "اطلاعات با موفقیت ذخیره شد.";
            saveErrorMessage = "خطا: ایمیل تکراری است.";
            serverErrorMessage = "خطا در اتصال به سرور";
        } else {
            wait = "Please wait...";
            emptyFieldsMessage = "Please fill out all fields";
            invalidEmailMessage = "The entered email is invalid";
            saveSuccessMessage = "Data saved successfully.";
            saveErrorMessage = "Error: Email is already taken.";
            serverErrorMessage = "Error connecting to the server";
        }
    }
}
