package com.example.healthcheckapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.healthcheckapp.utils.LocaleManager;
import com.example.healthcheckapp.utils.ThemeManager;

import android.os.Handler;
import android.os.Looper;

import androidx.cardview.widget.CardView;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextInputEditText etName, etSurname, etEmail, etPassword;
    private MaterialButton btnNext;
    private ProgressDialog progressDialog;
    private static String lastLanguage = null;

    private String emptyFieldsMessage, invalidEmailMessage, saveSuccessMessage, saveErrorMessage, serverErrorMessage , wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Apply the appropriate theme based on dark mode setting
        if (ThemeManager.isDarkMode(this)) {
            setTheme(R.style.Theme_MyApp_Dark);
        } else {
            setTheme(R.style.Theme_MyApp_Light);
        }

        // Set locale based on saved language preference
        String currentLang = LocaleManager.getLanguage(this);
        if (lastLanguage == null) {
            lastLanguage = currentLang;
        }
        LocaleManager.setLocale(this, currentLang);

        // Set messages based on the current language
        setMessagesBasedOnLanguage(currentLang);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CardView cardView = findViewById(R.id.cardView);

        // Set the background color based on dark mode
        if (ThemeManager.isDarkMode(this)) {
            cardView.setCardBackgroundColor(Color.parseColor("#0f4b6f"));
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        initViews();

        // Open the navigation drawer when the button is clicked
        findViewById(R.id.btnOpenMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(this::handleNavigation);

        // Handle "Next" button click event to validate and save data
        btnNext.setOnClickListener(v -> validateAndSaveData());

        // Hide "Register" menu item
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_register).setVisible(false);
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

    // Initialize views and components
    private void initViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnNext = findViewById(R.id.btnNext);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(wait);
        progressDialog.setCancelable(false);
    }

    // Handle navigation item selection
    private boolean handleNavigation(MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;

        if (itemId == R.id.nav_home) {
            intent = new Intent(MainActivity.this, TopScoresActivity.class);
        } else if (itemId == R.id.nav_login) {
            intent = new Intent(MainActivity.this, LoginActivity.class);
        } else if (itemId == R.id.nav_settings) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
        } else if (itemId == R.id.nav_logout) {
            finishAffinity();
            return true;
        }

        if (intent != null) {
            startActivity(intent);
        }

        // Deselect all menu items after a short delay
        new Handler().postDelayed(() -> {
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }, 300);

        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    // Validate form data and save to database
    private void validateAndSaveData() {
        String name = etName.getText().toString().trim();
        String surname = etSurname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Check if any fields are empty
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, emptyFieldsMessage, Toast.LENGTH_SHORT).show();
        }
        // Validate email format
        else if (!isValidEmail(email)) {
            Toast.makeText(this, invalidEmailMessage, Toast.LENGTH_SHORT).show();
        }
        // Save data to the database if validation is successful
        else {
            saveDataToDatabase(name, surname, email, password);
        }
    }

    // Check if email format is valid
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    // Save form data to the database using an API request
    private void saveDataToDatabase(String name, String surname, String email, String password) {
        String url = "https://b.mrbackend.ir/register.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.dismiss();
                    handleServerResponse(response, name, surname, email);
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, serverErrorMessage, Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Prepare data to send in the POST request
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("surname", surname);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    // Handle server response and redirect to the next screen
    private void handleServerResponse(String response, String name, String surname, String email) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.getString("status").equals("success")) {
                Toast.makeText(this, saveSuccessMessage, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, QuestionsActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("surname", surname);
                intent.putExtra("email", email);
                startActivity(intent);
            } else {
                Toast.makeText(this, saveErrorMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recreate activity if the language has changed
        String currentLang = LocaleManager.getLanguage(this);
        if (!currentLang.equals(lastLanguage)) {
            lastLanguage = currentLang;
            recreate();
        }
    }
}